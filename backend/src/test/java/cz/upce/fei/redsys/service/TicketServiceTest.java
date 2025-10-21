package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Project;
import cz.upce.fei.redsys.domain.Ticket;
import cz.upce.fei.redsys.domain.TicketPriority;
import cz.upce.fei.redsys.domain.TicketState;
import cz.upce.fei.redsys.domain.TicketType;
import cz.upce.fei.redsys.dto.TicketDto.CreateTicketRequest;
import cz.upce.fei.redsys.dto.TicketDto.PaginatedTicketResponse;
import cz.upce.fei.redsys.dto.TicketDto.UpdateTicketRequest;
import cz.upce.fei.redsys.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    private static final Long PROJECT_ID = 1L;
    private static final Long OTHER_PROJECT_ID = 2L;
    private static final Long TICKET_ID = 10L;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private TicketService ticketService;

    private Project mockProject;
    private Project otherProject;
    private Ticket mockTicket;

    @BeforeEach
    void setUp() {
        mockProject = Project.builder().id(PROJECT_ID).name("Project X").build();
        otherProject = Project.builder().id(OTHER_PROJECT_ID).name("Foreign Project").build();

        mockTicket = Ticket.builder()
                .id(TICKET_ID)
                .title("Initial Title")
                .type(TicketType.BUG)
                .priority(TicketPriority.HIGH)
                .state(TicketState.OPEN)
                .project(mockProject)
                .build();

        when(projectService.getOwnedProjectEntity(PROJECT_ID)).thenReturn(mockProject);
    }

    @Test
    void create_ShouldSaveNewTicketWithDefaultState() {
        CreateTicketRequest request = new CreateTicketRequest(
                "New Feature",
                TicketType.FEATURE,
                TicketPriority.MEDIUM
        );
        when(ticketRepository.save(any(Ticket.class))).thenReturn(mockTicket);

        ticketService.create(PROJECT_ID, request);

        verify(ticketRepository, times(1)).save(argThat(ticket ->
                ticket.getTitle().equals("New Feature") &&
                        ticket.getProject().getId().equals(PROJECT_ID) &&
                        ticket.getState() == TicketState.OPEN
        ));
    }

    @Test
    void list_ShouldReturnPaginatedTicketResponse() {
        List<Ticket> ticketList = List.of(mockTicket);
        Page<Ticket> ticketPage = new PageImpl<>(ticketList, pageable, 1L);

        when(ticketRepository.findAllByProject(mockProject, pageable)).thenReturn(ticketPage);

        PaginatedTicketResponse response = ticketService.list(PROJECT_ID, pageable);

        assertNotNull(response);
        assertEquals(1, response.totalElements());
        assertEquals(1, response.tickets().size());
        verify(projectService, times(1)).getOwnedProjectEntity(PROJECT_ID);
        verify(ticketRepository, times(1)).findAllByProject(mockProject, pageable);
    }

    @Test
    void get_ShouldReturnTicketResponse_WhenFoundAndOwned() {
        when(ticketRepository.findById(TICKET_ID)).thenReturn(Optional.of(mockTicket));

        ticketService.get(PROJECT_ID, TICKET_ID);

        verify(ticketRepository, times(1)).findById(TICKET_ID);
    }

    @Test
    void get_ShouldThrowNotFound_WhenTicketDoesNotExist() {
        when(ticketRepository.findById(TICKET_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                ticketService.get(PROJECT_ID, TICKET_ID)
        );
    }

    @Test
    void get_ShouldThrowAccessDenied_WhenTicketBelongsToOtherProject() {
        mockTicket.setProject(otherProject);
        when(ticketRepository.findById(TICKET_ID)).thenReturn(Optional.of(mockTicket));

        assertThrows(AccessDeniedException.class, () ->
                ticketService.get(PROJECT_ID, TICKET_ID)
        );
    }

    @Test
    void update_ShouldUpdateAllFieldsAndSave() {
        UpdateTicketRequest request = new UpdateTicketRequest(
                "Updated Title",
                TicketType.TASK,
                TicketPriority.LOW,
                TicketState.IN_PROGRESS
        );

        when(ticketRepository.findById(TICKET_ID)).thenReturn(Optional.of(mockTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(mockTicket);

        ticketService.update(PROJECT_ID, TICKET_ID, request);

        verify(ticketRepository, times(1)).save(argThat(ticket ->
                ticket.getTitle().equals("Updated Title") &&
                        ticket.getType() == TicketType.TASK &&
                        ticket.getState() == TicketState.IN_PROGRESS
        ));
    }

    @Test
    void update_ShouldThrowAccessDenied_WhenTicketBelongsToOtherProject() {
        UpdateTicketRequest request = new UpdateTicketRequest("T", TicketType.TASK, TicketPriority.LOW, TicketState.IN_PROGRESS);
        mockTicket.setProject(otherProject);
        when(ticketRepository.findById(TICKET_ID)).thenReturn(Optional.of(mockTicket));

        assertThrows(AccessDeniedException.class, () ->
                ticketService.update(PROJECT_ID, TICKET_ID, request)
        );
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void delete_ShouldCallRepositoryDelete_WhenOwned() {
        when(ticketRepository.findById(TICKET_ID)).thenReturn(Optional.of(mockTicket));
        doNothing().when(ticketRepository).delete(mockTicket);

        ticketService.delete(PROJECT_ID, TICKET_ID);

        verify(ticketRepository, times(1)).delete(mockTicket);
    }

    @Test
    void delete_ShouldThrowAccessDenied_WhenTicketBelongsToOtherProject() {
        mockTicket.setProject(otherProject);
        when(ticketRepository.findById(TICKET_ID)).thenReturn(Optional.of(mockTicket));

        assertThrows(AccessDeniedException.class, () ->
                ticketService.delete(PROJECT_ID, TICKET_ID)
        );
        verify(ticketRepository, never()).delete(any());
    }
}