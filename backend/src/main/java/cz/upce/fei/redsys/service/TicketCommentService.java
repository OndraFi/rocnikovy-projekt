package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Ticket;
import cz.upce.fei.redsys.domain.TicketComment;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.domain.UserRole;
import cz.upce.fei.redsys.dto.TicketCommentDto;
import cz.upce.fei.redsys.dto.TicketCommentDto.TicketCommentResponse;
import cz.upce.fei.redsys.dto.TicketCommentDto.PaginatedTicketCommentResponse;
import cz.upce.fei.redsys.dto.TicketCommentDto.CreateTicketCommentRequest;
import cz.upce.fei.redsys.dto.TicketCommentDto.UpdateTicketCommentRequest;
import cz.upce.fei.redsys.repository.TicketCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketService ticketService;
    private final AuthService authService;

    @Transactional
    public PaginatedTicketCommentResponse listComments(Long ticketId, Pageable pageable) {
        log.debug("Listing comments for ticket {}: {}", ticketId, pageable);
        Ticket ticket = ticketService.requireTicketById(ticketId);

        Page<TicketComment> page = commentRepository.findAllByTicketOrderByTicketCommentNumberDesc(ticket, pageable);
        List<TicketCommentResponse> comments = page.getContent().stream()
                .map(TicketCommentDto::toResponse)
                .toList();

        log.debug("Found {} comments", comments.size());

        return new PaginatedTicketCommentResponse(
                comments,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional
    public TicketCommentResponse createComment(Long ticketId, CreateTicketCommentRequest req) {
        Ticket ticket = ticketService.requireTicketById(ticketId);
        int nextNumber = commentRepository.findMaxTicketCommentNumberByTicket(ticket) + 1;

        log.debug("Creating comment for ticket {} with number {}", ticketId, nextNumber);

        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .author(authService.currentUser())
                .content(req.content())
                .ticketCommentNumber(nextNumber)
                .build();

        TicketCommentResponse response = TicketCommentDto.toResponse(commentRepository.save(comment));
        log.debug("Comment created: {}", response);
        return response;
    }

    @Transactional
    public TicketCommentResponse updateComment(Long ticketId, Integer commentNumber, UpdateTicketCommentRequest req) {
        TicketComment comment = findComment(ticketId, commentNumber);
        User currentUser = authService.currentUser();
        log.debug("Updating comment {} for ticket {}", commentNumber, ticketId);

        if (!currentUser.getRole().equals(UserRole.ADMIN) && !currentUser.getId().equals(comment.getAuthor().getId())) {
            throw new AccessDeniedException("You can only edit your own comments");
        }

        comment.setContent(req.content());

        TicketCommentResponse response = TicketCommentDto.toResponse(commentRepository.save(comment));
        log.debug("Comment updated: {}", response);
        return response;
    }

    @Transactional
    public void deleteComment(Long ticketId, Integer commentNumber) {
        TicketComment comment = findComment(ticketId, commentNumber);
        User currentUser = authService.currentUser();
        log.debug("Deleting comment {} for ticket {}", commentNumber, ticketId);

        if (!currentUser.getRole().equals(UserRole.ADMIN) && !currentUser.getId().equals(comment.getAuthor().getId())) {
            throw new AccessDeniedException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    private TicketComment findComment(Long ticketId, Integer commentNumber) {
        Ticket ticket = ticketService.requireTicketById(ticketId);
        return commentRepository.findByTicketAndTicketCommentNumber(ticket, commentNumber)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }
}
