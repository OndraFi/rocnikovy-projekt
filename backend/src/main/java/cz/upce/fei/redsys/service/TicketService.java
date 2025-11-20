package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Article;
import cz.upce.fei.redsys.domain.Ticket;
import cz.upce.fei.redsys.domain.TicketState;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.dto.TicketDto;
import cz.upce.fei.redsys.dto.TicketDto.CreateTicketRequest;
import cz.upce.fei.redsys.dto.TicketDto.TicketResponse;
import cz.upce.fei.redsys.dto.TicketDto.PaginatedTicketResponse;
import cz.upce.fei.redsys.dto.TicketDto.UpdateTicketRequest;
import cz.upce.fei.redsys.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cz.upce.fei.redsys.dto.TicketDto.toTicketResponse;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final AuthService authService;
    private final ArticleService articleService;

    @Transactional
    public TicketResponse create(CreateTicketRequest req) {
        log.debug("Creating ticket with title '{}'", req.title());
        User assignee = (req.assigneeUsername() != null && !req.assigneeUsername().isBlank()) ?
                userService.requireUserByIdentifier(req.assigneeUsername()) : null;
        Article article = articleService.requireArticleById(req.articleId());

        Ticket ticket = Ticket.builder()
                .title(req.title())
                .description(req.description())
                .state(TicketState.OPEN)
                .assignee(assignee)
                .author(authService.currentUser())
                .article(article)
                .build();

        TicketResponse response = toTicketResponse(ticketRepository.save(ticket));
        log.debug("Created ticket: {}", response);
        return response;
    }

    public PaginatedTicketResponse list(Pageable pageable) {
        log.debug("Listing tickets: {}", pageable);
        Page<Ticket> ticketPage = ticketRepository.findAll(pageable);

        List<TicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(TicketDto::toTicketResponse)
                .toList();

        log.debug("Found {} tickets", ticketResponses.size());

        return new PaginatedTicketResponse(
                ticketResponses,
                ticketPage.getNumber(),
                ticketPage.getSize(),
                ticketPage.getTotalElements(),
                ticketPage.getTotalPages()
        );
    }

    public TicketResponse get(Long id) {
        log.debug("Getting ticket with id {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        TicketResponse response = toTicketResponse(ticket);
        log.debug("Ticket found: {}", response);
        return response;
    }

    @Transactional
    public TicketResponse update(Long id, UpdateTicketRequest req) {
        log.debug("Updating ticket with id {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        ticket.setTitle(req.title());
        ticket.setDescription(req.description());

        User assignee = (req.assigneeUsername() != null && !req.assigneeUsername().isBlank()) ?
                userService.requireUserByIdentifier(req.assigneeUsername()) : null;
        ticket.setAssignee(assignee);

        TicketResponse response = toTicketResponse(ticketRepository.save(ticket));
        log.debug("Updated ticket: {}", response);
        return response;
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Deleting ticket with id {}", id);
        if (!ticketRepository.existsById(id)) {
            throw new EntityNotFoundException("Ticket not found");
        }
        ticketRepository.deleteById(id);
    }

    public Ticket requireTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
    }
}