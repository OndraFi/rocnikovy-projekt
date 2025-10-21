package cz.upce.fei.redsys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.fei.redsys.dto.TicketDto.CreateTicketRequest;
import cz.upce.fei.redsys.dto.TicketDto.TicketResponse;
import cz.upce.fei.redsys.domain.TicketType;
import cz.upce.fei.redsys.domain.TicketPriority;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class TicketControllerTest {

    private static final String API_BASE = "/api/projects/{projectId}/tickets";
    private static final Long PROJECT_ID = 1L;
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
                TicketType.FEATURE,
                TicketPriority.MEDIUM,
                TicketState.OPEN,
                PROJECT_ID
        );
    }

    @Test
    @WithMockUser
    void create_ShouldReturnCreatedTicketAnd201() throws Exception {
        CreateTicketRequest request = new CreateTicketRequest(
                "New Ticket Title",
                TicketType.BUG,
                TicketPriority.HIGH
        );

        when(ticketService.create(eq(PROJECT_ID), any(CreateTicketRequest.class)))
                .thenReturn(mockTicketResponse);

        mockMvc.perform(post(API_BASE, PROJECT_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/projects/" + PROJECT_ID + "/tickets/" + TICKET_ID))
                .andExpect(jsonPath("$.id").value(TICKET_ID))
                .andExpect(jsonPath("$.title").value("Test Ticket"));
    }

    @Test
    @WithMockUser
    void get_ShouldReturnTicketAnd200() throws Exception {
        when(ticketService.get(PROJECT_ID, TICKET_ID)).thenReturn(mockTicketResponse);

        mockMvc.perform(get(API_BASE + "/{ticketId}", PROJECT_ID, TICKET_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TICKET_ID))
                .andExpect(jsonPath("$.title").value("Test Ticket"));
    }

    @Test
    @WithMockUser
    void create_ShouldReturn400_WhenTitleIsMissing() throws Exception {
        CreateTicketRequest invalidRequest = new CreateTicketRequest(
                "",
                TicketType.BUG,
                TicketPriority.HIGH
        );

        mockMvc.perform(post(API_BASE, PROJECT_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("The 'title' field is required"));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturn204() throws Exception {
        mockMvc.perform(delete(API_BASE + "/{ticketId}", PROJECT_ID, TICKET_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(ticketService, times(1)).delete(PROJECT_ID, TICKET_ID);
    }
}