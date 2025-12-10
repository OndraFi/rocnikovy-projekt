package cz.upce.fei.redsys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.fei.redsys.domain.UserRole;
import cz.upce.fei.redsys.dto.TicketCommentDto.*;
import cz.upce.fei.redsys.dto.UserDto.UserResponse;
import cz.upce.fei.redsys.service.TicketCommentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class TicketCommentControllerTest {

    private static final Long TEST_TICKET_ID = 1L;
    private static final Integer TEST_COMMENT_NUMBER = 1;
    private static final String TEST_CONTENT = "This is a test comment";
    private static final String TEST_USERNAME = "testUser";
    private static final Instant TEST_CREATED_AT = Instant.parse("2024-12-10T12:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TicketCommentService ticketCommentService;

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void list_ShouldReturnPaginatedCommentsAnd200() throws Exception {
        UserResponse author1 = new UserResponse(1L, "author1", "Author One", UserRole.USER);
        UserResponse author2 = new UserResponse(2L, "author2", "Author Two", UserRole.USER);

        TicketCommentResponse comment1 = TicketCommentResponse.builder()
                .number(1)
                .content("First comment")
                .createdAt(TEST_CREATED_AT)
                .updatedAt(TEST_CREATED_AT)
                .author(author1)
                .build();

        TicketCommentResponse comment2 = TicketCommentResponse.builder()
                .number(2)
                .content("Second comment")
                .createdAt(TEST_CREATED_AT.plusSeconds(3600))
                .updatedAt(TEST_CREATED_AT.plusSeconds(3600))
                .author(author2)
                .build();

        PaginatedTicketCommentResponse mockResponse = new PaginatedTicketCommentResponse(
                List.of(comment1, comment2),
                0,
                20,
                2L,
                1
        );

        when(ticketCommentService.listComments(eq(TEST_TICKET_ID), any(Pageable.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/tickets/{ticketId}/comments", TEST_TICKET_ID)
                        .with(csrf())
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments.length()").value(2))
                .andExpect(jsonPath("$.comments[0].number").value(1))
                .andExpect(jsonPath("$.comments[0].content").value("First comment"))
                .andExpect(jsonPath("$.comments[0].author.username").value("author1"))
                .andExpect(jsonPath("$.comments[1].number").value(2))
                .andExpect(jsonPath("$.comments[1].content").value("Second comment"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void create_ShouldReturnCommentAnd201() throws Exception {
        CreateTicketCommentRequest request = new CreateTicketCommentRequest(TEST_CONTENT);
        UserResponse authorResponse = new UserResponse(1L, TEST_USERNAME, "Test User", UserRole.USER);

        TicketCommentResponse mockResponse = TicketCommentResponse.builder()
                .number(TEST_COMMENT_NUMBER)
                .content(TEST_CONTENT)
                .createdAt(TEST_CREATED_AT)
                .updatedAt(TEST_CREATED_AT)
                .author(authorResponse)
                .build();

        when(ticketCommentService.createComment(eq(TEST_TICKET_ID), any(CreateTicketCommentRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/tickets/{ticketId}/comments", TEST_TICKET_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value(TEST_COMMENT_NUMBER))
                .andExpect(jsonPath("$.content").value(TEST_CONTENT))
                .andExpect(jsonPath("$.author.username").value(TEST_USERNAME));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void update_ShouldReturnUpdatedCommentAnd200() throws Exception {
        UpdateTicketCommentRequest request = new UpdateTicketCommentRequest("Updated comment content");
        UserResponse authorResponse = new UserResponse(1L, TEST_USERNAME, "Test User", UserRole.USER);

        TicketCommentResponse mockResponse = TicketCommentResponse.builder()
                .number(TEST_COMMENT_NUMBER)
                .content("Updated comment content")
                .createdAt(TEST_CREATED_AT)
                .updatedAt(TEST_CREATED_AT.plusSeconds(3600))
                .author(authorResponse)
                .build();

        when(ticketCommentService.updateComment(
                eq(TEST_TICKET_ID),
                eq(TEST_COMMENT_NUMBER),
                any(UpdateTicketCommentRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(put("/api/tickets/{ticketId}/comments/{commentNumber}",
                        TEST_TICKET_ID, TEST_COMMENT_NUMBER)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(TEST_COMMENT_NUMBER))
                .andExpect(jsonPath("$.content").value("Updated comment content"));
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(ticketCommentService).deleteComment(TEST_TICKET_ID, TEST_COMMENT_NUMBER);

        mockMvc.perform(delete("/api/tickets/{ticketId}/comments/{commentNumber}",
                        TEST_TICKET_ID, TEST_COMMENT_NUMBER)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void list_ShouldReturn404WhenTicketNotFound() throws Exception {
        when(ticketCommentService.listComments(eq(TEST_TICKET_ID), any(Pageable.class)))
                .thenThrow(new EntityNotFoundException("Ticket not found"));

        mockMvc.perform(get("/api/tickets/{ticketId}/comments", TEST_TICKET_ID)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void create_ShouldReturn404WhenTicketNotFound() throws Exception {
        CreateTicketCommentRequest request = new CreateTicketCommentRequest(TEST_CONTENT);

        when(ticketCommentService.createComment(eq(TEST_TICKET_ID), any(CreateTicketCommentRequest.class)))
                .thenThrow(new EntityNotFoundException("Ticket not found"));

        mockMvc.perform(post("/api/tickets/{ticketId}/comments", TEST_TICKET_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void update_ShouldReturn404WhenCommentNotFound() throws Exception {
        UpdateTicketCommentRequest request = new UpdateTicketCommentRequest("Updated content");

        when(ticketCommentService.updateComment(
                eq(TEST_TICKET_ID),
                eq(TEST_COMMENT_NUMBER),
                any(UpdateTicketCommentRequest.class)))
                .thenThrow(new EntityNotFoundException("Comment not found"));

        mockMvc.perform(put("/api/tickets/{ticketId}/comments/{commentNumber}",
                        TEST_TICKET_ID, TEST_COMMENT_NUMBER)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void delete_ShouldReturn404WhenCommentNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Comment not found"))
                .when(ticketCommentService).deleteComment(TEST_TICKET_ID, TEST_COMMENT_NUMBER);

        mockMvc.perform(delete("/api/tickets/{ticketId}/comments/{commentNumber}",
                        TEST_TICKET_ID, TEST_COMMENT_NUMBER)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = TEST_USERNAME)
    void list_EmptyResult_ShouldReturnEmptyArrayAnd200() throws Exception {
        PaginatedTicketCommentResponse mockResponse = new PaginatedTicketCommentResponse(
                List.of(),
                0,
                20,
                0L,
                0
        );

        when(ticketCommentService.listComments(eq(TEST_TICKET_ID), any(Pageable.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/tickets/{ticketId}/comments", TEST_TICKET_ID)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
}