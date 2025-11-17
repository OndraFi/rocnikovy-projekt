package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Ticket;
import cz.upce.fei.redsys.domain.TicketComment;
import cz.upce.fei.redsys.dto.TicketCommentDto;
import cz.upce.fei.redsys.dto.TicketCommentDto.TicketCommentResponse;
import cz.upce.fei.redsys.dto.TicketCommentDto.PaginatedTicketCommentResponse;
import cz.upce.fei.redsys.dto.TicketCommentDto.CreateTicketCommentRequest;
import cz.upce.fei.redsys.repository.TicketCommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
}
