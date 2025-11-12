package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Ticket;
import cz.upce.fei.redsys.domain.TicketComment;
import cz.upce.fei.redsys.dto.TicketCommentDto;
import cz.upce.fei.redsys.dto.TicketCommentDto.TicketCommentResponse;
import cz.upce.fei.redsys.dto.TicketCommentDto.CreateTicketCommentRequest;
import cz.upce.fei.redsys.repository.TicketCommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketService ticketService;
    private final AuthService authService;

    @Transactional
    public Page<TicketCommentResponse> listComments(Long ticketId, Pageable pageable) {
        Ticket ticket = ticketService.requireTicketById(ticketId);

        return commentRepository.findAllByTicketOrderByTicketCommentNumberDesc(ticket, pageable)
                .map(TicketCommentDto::toResponse);
    }

    @Transactional
    public TicketCommentResponse createComment(Long ticketId, CreateTicketCommentRequest req) {
        Ticket ticket = ticketService.requireTicketById(ticketId);
        int nextNumber = commentRepository.findMaxTicketCommentNumberByTicket(ticket) + 1;

        TicketComment comment = TicketComment.builder()
                .ticket(ticket)
                .author(authService.currentUser())
                .content(req.content())
                .ticketCommentNumber(nextNumber)
                .build();

        return TicketCommentDto.toResponse(commentRepository.save(comment));
    }
}
