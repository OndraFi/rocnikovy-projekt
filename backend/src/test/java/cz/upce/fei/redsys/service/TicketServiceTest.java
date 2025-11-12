package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Ticket;
import cz.upce.fei.redsys.domain.TicketState;
import cz.upce.fei.redsys.domain.User;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    private static final Long TICKET_ID = 10L;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private TicketService ticketService;

    private Ticket mockTicket;
    private User mockAssignee;
    private User mockAuthor;

    @BeforeEach
    void setUp() {
        mockAssignee = User.builder().id(2L).username("john.doe").build();
        mockAuthor = User.builder().id(1L).username("alice").build();

        mockTicket = Ticket.builder()
                .id(TICKET_ID)
                .title("Initial Title")
                .description("Initial Description")
                .state(TicketState.OPEN)
                .assignee(mockAssignee)
                .author(mockAuthor)
                .build();
    }

    @Test
    void create_ShouldSaveNewTicketWithDefaultState() {
        CreateTicketRequest request = new CreateTicketRequest(
                "New Feature",
                "Some description",
                "john.doe",
                1L
        );

        when(userService.requireUserByIdentifier("john.doe")).thenReturn(mockAssignee);
        when(authService.currentUser()).thenReturn(mockAuthor);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(mockTicket);

        ticketService.create(request);

        verify(ticketRepository, times(1)).save(argThat(ticket ->
                ticket.getTitle().equals("New Feature") &&
                        ticket.getAssignee().getUsername().equals("john.doe") &&
                        ticket.getState() == TicketState.OPEN
        ));
    }

    @Test
    void list_ShouldReturnPaginatedTicketResponse() {
        List<Ticket> ticketList = List.of(mockTicket);
        Page<Ticket> ticketPage = new PageImpl<>(ticketList, pageable, 1L);

        when(ticketRepository.findAll(pageable)).thenReturn(ticketPage);

        PaginatedTicketResponse response = ticketService.list(pageable);

        assertNotNull(response);
        assertEquals(1, response.totalElements());
        assertEquals(1, response.tickets().size());
        verify(ticketRepository, times(1)).findAll(pageable);
    }

    @Test
    void get_ShouldReturnTicketResponse_WhenFound() {
        when(ticketRepository.findById(TICKET_ID)).thenReturn(Optional.of(mockTicket));

        ticketService.get(TICKET_ID);

        verify(ticketRepository, times(1)).findById(TICKET_ID);
    }

    @Test
    void get_ShouldThrowNotFound_WhenTicketDoesNotExist() {
        when(ticketRepository.findById(TICKET_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                ticketService.get(TICKET_ID)
        );
    }

    @Test
    void update_ShouldUpdateAllFieldsAndSave() {
        UpdateTicketRequest request = new UpdateTicketRequest(
                "Updated Title",
                "Updated Description",
                TicketState.IN_PROGRESS,
                "john.doe"
        );

        when(ticketRepository.findById(TICKET_ID)).thenReturn(Optional.of(mockTicket));
        when(userService.requireUserByIdentifier("john.doe")).thenReturn(mockAssignee);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(mockTicket);

        ticketService.update(TICKET_ID, request);

        verify(ticketRepository, times(1)).save(argThat(ticket ->
                ticket.getTitle().equals("Updated Title") &&
                        ticket.getDescription().equals("Updated Description") &&
                        ticket.getState() == TicketState.IN_PROGRESS &&
                        ticket.getAssignee().getUsername().equals("john.doe")
        ));
    }

    @Test
    void delete_ShouldCallRepositoryDelete_WhenFound() {
        when(ticketRepository.existsById(TICKET_ID)).thenReturn(true);
        doNothing().when(ticketRepository).deleteById(TICKET_ID);

        ticketService.delete(TICKET_ID);

        verify(ticketRepository, times(1)).deleteById(TICKET_ID);
    }

    @Test
    void delete_ShouldThrowNotFound_WhenTicketDoesNotExist() {
        when(ticketRepository.existsById(TICKET_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
                ticketService.delete(TICKET_ID)
        );
        verify(ticketRepository, never()).delete(any());
    }
}