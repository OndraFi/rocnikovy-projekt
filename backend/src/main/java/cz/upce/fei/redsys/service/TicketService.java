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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cz.upce.fei.redsys.dto.TicketDto.toTicketResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final AuthService authService;
    private final ArticleService articleService;

    @Transactional
    public TicketResponse create(CreateTicketRequest req) {
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

        return toTicketResponse(ticketRepository.save(ticket));
    }

    public PaginatedTicketResponse list(Pageable pageable) {
        Page<Ticket> ticketPage = ticketRepository.findAll(pageable);

        List<TicketResponse> ticketResponses = ticketPage.getContent().stream()
                .map(TicketDto::toTicketResponse)
                .toList();

        return new PaginatedTicketResponse(
                ticketResponses,
                ticketPage.getNumber(),
                ticketPage.getSize(),
                ticketPage.getTotalElements(),
                ticketPage.getTotalPages()
        );
    }

    public TicketResponse get(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
        return toTicketResponse(ticket);
    }

    @Transactional
    public TicketResponse update(Long id, UpdateTicketRequest req) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));

        ticket.setTitle(req.title());
        ticket.setDescription(req.description());
        ticket.setState(req.state());

        User assignee = (req.assigneeUsername() != null && !req.assigneeUsername().isBlank()) ?
                userService.requireUserByIdentifier(req.assigneeUsername()) : null;
        ticket.setAssignee(assignee);

        return toTicketResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public void delete(Long id) {
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