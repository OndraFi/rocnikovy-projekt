package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.*;
import cz.upce.fei.redsys.dto.TicketCommentDto.CreateTicketCommentRequest;
import cz.upce.fei.redsys.dto.TicketDto;
import cz.upce.fei.redsys.dto.TicketDto.TicketResponse;
import cz.upce.fei.redsys.repository.ArticleRepository;
import cz.upce.fei.redsys.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final TicketRepository ticketRepository;
    private final ArticleRepository articleRepository;
    private final AuthService authService;
    private final ArticleVersionService articleVersionService;
    private final TicketService ticketService;
    private final TicketCommentService ticketCommentService;

    private final Map<TicketState, Set<TicketState>> allowedStateTransitions = Map.of(
            TicketState.OPEN, Set.of(TicketState.IN_PROGRESS),
            TicketState.IN_PROGRESS, Set.of(TicketState.FOR_REVIEW, TicketState.OPEN),
            TicketState.FOR_REVIEW, Set.of(TicketState.IN_PROGRESS, TicketState.APPROVED),
            TicketState.APPROVED, Set.of(TicketState.PUBLISHED),
            TicketState.PUBLISHED, Collections.emptySet()
    );

    private final Map<TicketState, Set<UserRole>> allowedStateRoles = Map.of(
            TicketState.IN_PROGRESS, Set.of(UserRole.EDITOR, UserRole.CHIEF_EDITOR),
            TicketState.FOR_REVIEW, Set.of(UserRole.EDITOR),
            TicketState.APPROVED, Set.of(UserRole.CHIEF_EDITOR),
            TicketState.PUBLISHED, Set.of(UserRole.CHIEF_EDITOR),
            TicketState.OPEN, Set.of(UserRole.CHIEF_EDITOR)
    );

    @Transactional
    public TicketResponse transition(Long ticketId, TicketState targetState, String comment) {
        Ticket ticket = ticketService.requireTicketById(ticketId);
        TicketState currentState = ticket.getState();
        User currentUser = authService.currentUser();

        log.debug("Changing state {} -> {} for ticket {}", currentState, targetState, ticketId);

        if (currentState == targetState) return TicketDto.toTicketResponse(ticket);

        checkStateTransitionAllowed(currentState, targetState);
        checkRoleAllowed(targetState, currentUser);
        checkTicketOwnership(ticket, targetState, currentUser);

        performSideEffects(ticket, ticket.getArticle(), currentState, targetState, currentUser);

        ticket.setState(targetState);
        Ticket saved = ticketRepository.save(ticket);

        if (comment != null && !comment.isBlank()) {
            CreateTicketCommentRequest commentRequest = new CreateTicketCommentRequest(comment);
            ticketCommentService.createComment(ticket.getId(), commentRequest);
        }

        log.info("Ticket ({}) state changed: {} -> {} by {}", ticketId, currentState, targetState, currentUser.getUsername());
        return TicketDto.toTicketResponse(saved);
    }

    private void checkStateTransitionAllowed(TicketState current, TicketState target) {
        Set<TicketState> allowedTargets = allowedStateTransitions.getOrDefault(current, Collections.emptySet());
        if (!allowedTargets.contains(target)) {
            throw new WorkflowException("Invalid state transition: " + current + " -> " + target);
        }
    }

    private void checkRoleAllowed(TicketState target, User user) {
        if (user.getRole() == UserRole.ADMIN) return;
        Set<UserRole> roles = allowedStateRoles.getOrDefault(target, Collections.emptySet());
        if (!roles.isEmpty() && !roles.contains(user.getRole())) {
            throw new AccessDeniedException("You don't have permission to change the ticket state to " + target);
        }
    }

    private void checkTicketOwnership(Ticket ticket, TicketState targetState, User user) {
        if (user.getRole() == UserRole.ADMIN) return;
        switch (user.getRole()) {
            case EDITOR -> {
                if (ticket.getAssignee() == null || !ticket.getAssignee().getId().equals(user.getId())) {
                    throw new AccessDeniedException("You can only change ticket state on tickets assigned to you");
                }
            }
            case REVIEWER -> {
                if (targetState != TicketState.IN_PROGRESS && ticket.getState() != TicketState.FOR_REVIEW) {
                    throw new AccessDeniedException("You can only change ticket state on tickets in FOR_REVIEW state");
                }
            }
            case CHIEF_EDITOR -> {}
            default -> throw new AccessDeniedException("You don't have permission to change the ticket state");
        }
    }

    private void performSideEffects(Ticket ticket, Article article, TicketState from, TicketState to, User actor) {
        switch (to) {
            case IN_PROGRESS -> {
                if (from == TicketState.FOR_REVIEW) {
                    article.setArticleState(ArticleState.DRAFT);
                    articleRepository.save(article);
                } else if (ticket.getAssignee() == null && actor.getRole() == UserRole.EDITOR) {
                    ticket.setAssignee(actor);
                }
            }

            case FOR_REVIEW -> {
                article.setArticleState(ArticleState.IN_REVIEW);
                articleRepository.save(article);
                createArticleVersion(article);
            }

            case PUBLISHED -> {
                article.setArticleState(ArticleState.PUBLISHED);
                article.setPublishedAt(Instant.now());
                articleRepository.save(article);
                createArticleVersion(article);
            }

            default -> {}
        }
    }

    private void createArticleVersion(Article article) {
            ArticleVersion latest = articleVersionService.getLatestVersion(article);
            articleVersionService.createNewVersionIfChanged(article, latest.getContent());
    }

    public static class WorkflowException extends RuntimeException {
        public WorkflowException(String message) {
            super(message);
        }
    }
}
