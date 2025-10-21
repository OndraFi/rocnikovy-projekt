package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Project;
import cz.upce.fei.redsys.domain.Ticket;
import cz.upce.fei.redsys.domain.TicketState;
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
    private final ProjectService projectService;

    @Transactional
    public TicketResponse create(Long projectId, CreateTicketRequest req) {
        Project project = projectService.getOwnedProjectEntity(projectId);

        Long nextNumber = ticketRepository.findMaxProjectTicketNumber(project)
                .map(n -> n + 1)
                .orElse(1L);

        Ticket ticket = Ticket.builder()
                .title(req.title())
                .type(req.type())
                .priority(req.priority())
                .state(TicketState.OPEN)
                .project(project)
                .projectTicketNumber(nextNumber)
                .build();

        return toTicketResponse(ticketRepository.save(ticket));
    }

    public PaginatedTicketResponse list(Long projectId, Pageable pageable) {
        Project project = projectService.getOwnedProjectEntity(projectId);
        Page<Ticket> ticketPage = ticketRepository.findAllByProject(project, pageable);

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

    public TicketResponse get(Long projectId, Long projectTicketNumber) {
        Ticket ticket = getTicketByProjectAndNumber(projectId, projectTicketNumber);
        return toTicketResponse(ticket);
    }

    @Transactional
    public TicketResponse update(Long projectId, Long projectTicketNumber, UpdateTicketRequest req) {
        Ticket ticket = getTicketByProjectAndNumber(projectId, projectTicketNumber);

        ticket.setTitle(req.title());
        ticket.setType(req.type());
        ticket.setPriority(req.priority());
        ticket.setState(req.state());

        return toTicketResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public void delete(Long projectId, Long projectTicketNumber) {
        Ticket ticket = getTicketByProjectAndNumber(projectId, projectTicketNumber);
        ticketRepository.delete(ticket);
    }

    private Ticket getTicketByProjectAndNumber(Long projectId, Long projectTicketNumber) {
        Project project = projectService.getOwnedProjectEntity(projectId);
        return ticketRepository.findByProjectAndProjectTicketNumber(project, projectTicketNumber)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found"));
    }
}