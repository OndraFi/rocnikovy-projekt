package cz.upce.fei.redsys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.fei.redsys.domain.TicketFilterType;
import cz.upce.fei.redsys.dto.TicketDto.CreateTicketRequest;
import cz.upce.fei.redsys.dto.TicketDto.TicketResponse;
import cz.upce.fei.redsys.domain.TicketState;
import cz.upce.fei.redsys.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import cz.upce.fei.redsys.dto.TicketDto.PaginatedTicketResponse;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class TicketControllerTest {

    private static final Long TICKET_ID = 10L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TicketService ticketService;

    private TicketResponse mockTicketResponse;

    @BeforeEach
    void setUp() {
        mockTicketResponse = new TicketResponse(
                TICKET_ID,
                "Test Ticket",
                "Some description",
                Instant.now(),
                null,
                TicketState.OPEN,
                null,
                null,
                1L
        );
    }

    @Test
    @WithMockUser
    void create_ShouldReturnCreatedTicketAnd201() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest(
                "New Ticket Title",
                "Some description",
                "john.doe",
                1L
        );

        when(ticketService.create(any(CreateTicketRequest.class)))
                .thenReturn(mockTicketResponse);

        mockMvc.perform(post("/api/tickets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/tickets/" + TICKET_ID))
                .andExpect(jsonPath("$.id").value(TICKET_ID))
                .andExpect(jsonPath("$.title").value("Test Ticket"));
    }

    @Test
    @WithMockUser
    void get_ShouldReturnTicketAnd200() throws Exception {
        when(ticketService.get(TICKET_ID)).thenReturn(mockTicketResponse);

        mockMvc.perform(get("/api/tickets/{ticketId}", TICKET_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TICKET_ID))
                .andExpect(jsonPath("$.title").value("Test Ticket"));
    }

    @Test
    @WithMockUser
    void create_ShouldReturn400_WhenTitleIsMissing() throws Exception {
        CreateTicketRequest invalidRequest = new CreateTicketRequest(
                "",
                "Some description",
                "john.doe",
                1L
        );

        mockMvc.perform(post("/api/tickets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }



    @Test
    @WithMockUser
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/tickets/{ticketId}", TICKET_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(ticketService, times(1)).delete(TICKET_ID);
    }


    @Test
    @WithMockUser
    void listMyTickets_ShouldCallServiceWithDefaultAllAndReturn200() throws Exception {
        PaginatedTicketResponse response = new PaginatedTicketResponse(
                List.of(mockTicketResponse),
                0,
                20,
                1,
                1
        );

        when(ticketService.listMyTickets(eq(TicketFilterType.ALL), any())).thenReturn(response);

        mockMvc.perform(get("/api/tickets/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tickets[0].id").value(TICKET_ID))
                .andExpect(jsonPath("$.tickets[0].title").value("Test Ticket"));

        verify(ticketService, times(1)).listMyTickets(eq(TicketFilterType.ALL), any());
    }

    @Test
    @WithMockUser
    void listMyTickets_ShouldUseFilterTypeAssigned() throws Exception {
        PaginatedTicketResponse response = new PaginatedTicketResponse(
                List.of(mockTicketResponse),
                0,
                20,
                1,
                1
        );

        when(ticketService.listMyTickets(eq(TicketFilterType.ASSIGNED), any())).thenReturn(response);

        mockMvc.perform(get("/api/tickets/my")
                        .param("filterType", "ASSIGNED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tickets[0].id").value(TICKET_ID));

        verify(ticketService, times(1)).listMyTickets(eq(TicketFilterType.ASSIGNED), any());
    }

    @Test
    @WithMockUser
    void listMyTickets_ShouldUseFilterTypeOwned() throws Exception {
        PaginatedTicketResponse response = new PaginatedTicketResponse(
                List.of(mockTicketResponse),
                0,
                20,
                1,
                1
        );

        when(ticketService.listMyTickets(eq(TicketFilterType.OWNED), any())).thenReturn(response);

        mockMvc.perform(get("/api/tickets/my")
                        .param("filterType", "OWNED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tickets[0].id").value(TICKET_ID));

        verify(ticketService, times(1)).listMyTickets(eq(TicketFilterType.OWNED), any());
    }
}