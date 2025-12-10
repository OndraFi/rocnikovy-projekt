package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Ticket;
import cz.upce.fei.redsys.domain.TicketComment;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.domain.UserRole;
import cz.upce.fei.redsys.dto.TicketCommentDto.CreateTicketCommentRequest;
import cz.upce.fei.redsys.dto.TicketCommentDto.PaginatedTicketCommentResponse;
import cz.upce.fei.redsys.dto.TicketCommentDto.TicketCommentResponse;
import cz.upce.fei.redsys.dto.TicketCommentDto.UpdateTicketCommentRequest;
import cz.upce.fei.redsys.repository.TicketCommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketCommentServiceTest {

    private static final Long TICKET_ID = 1L;
    private static final Integer COMMENT_NUMBER_1 = 1;
    private static final Integer COMMENT_NUMBER_2 = 2;

    @Mock
    private TicketCommentRepository commentRepository;

    @Mock
    private TicketService ticketService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private TicketCommentService ticketCommentService;

    private Ticket ticket;
    private User author;
    private User admin;
    private User otherUser;
    private TicketComment comment1;
    private TicketComment comment2;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setId(TICKET_ID);

        author = User.builder()
                .id(1L)
                .username("author")
                .role(UserRole.USER)
                .build();

        admin = User.builder()
                .id(2L)
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        otherUser = User.builder()
                .id(3L)
                .username("other")
                .role(UserRole.USER)
                .build();

        comment1 = TicketComment.builder()
                .id(10L)
                .ticket(ticket)
                .author(author)
                .content("First comment")
                .ticketCommentNumber(COMMENT_NUMBER_1)
                .build();

        comment2 = TicketComment.builder()
                .id(11L)
                .ticket(ticket)
                .author(author)
                .content("Second comment")
                .ticketCommentNumber(COMMENT_NUMBER_2)
                .build();
    }

    @Test
    void listComments_ShouldReturnPaginatedComments() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("ticketCommentNumber").descending());
        Page<TicketComment> page = new PageImpl<>(List.of(comment2, comment1), pageable, 2);

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(commentRepository.findAllByTicketOrderByTicketCommentNumberDesc(ticket, pageable))
                .thenReturn(page);

        PaginatedTicketCommentResponse response =
                ticketCommentService.listComments(TICKET_ID, pageable);

        assertNotNull(response);
        assertEquals(2, response.comments().size());
        assertEquals(0, response.page());
        assertEquals(20, response.size());
        assertEquals(2, response.totalElements());
        assertEquals(1, response.totalPages());
        verify(ticketService, times(1)).requireTicketById(TICKET_ID);
        verify(commentRepository, times(1))
                .findAllByTicketOrderByTicketCommentNumberDesc(ticket, pageable);
    }

    @Test
    void listComments_ShouldReturnEmptyList_WhenNoComments() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<TicketComment> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(commentRepository.findAllByTicketOrderByTicketCommentNumberDesc(ticket, pageable))
                .thenReturn(emptyPage);

        PaginatedTicketCommentResponse response =
                ticketCommentService.listComments(TICKET_ID, pageable);

        assertNotNull(response);
        assertEquals(0, response.comments().size());
        assertEquals(0, response.totalElements());
        verify(ticketService, times(1)).requireTicketById(TICKET_ID);
        verify(commentRepository, times(1))
                .findAllByTicketOrderByTicketCommentNumberDesc(ticket, pageable);
    }

    @Test
    void createComment_ShouldCreateCommentWithNextNumberAndCurrentUser() {
        CreateTicketCommentRequest request =
                new CreateTicketCommentRequest("New comment");

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(commentRepository.findMaxTicketCommentNumberByTicket(ticket))
                .thenReturn(COMMENT_NUMBER_2);
        when(authService.currentUser()).thenReturn(author);
        when(commentRepository.save(any(TicketComment.class))).thenAnswer(invocation -> {
            TicketComment c = invocation.getArgument(0);
            c.setId(12L);
            return c;
        });

        TicketCommentResponse response =
                ticketCommentService.createComment(TICKET_ID, request);

        assertNotNull(response);
        assertEquals("New comment", response.content());
        assertEquals(COMMENT_NUMBER_2 + 1, response.number());
        assertEquals(author.getId(), response.author().id());
        verify(ticketService, times(1)).requireTicketById(TICKET_ID);
        verify(commentRepository, times(1))
                .findMaxTicketCommentNumberByTicket(ticket);
        verify(commentRepository, times(1)).save(any(TicketComment.class));
    }

    @Test
    void updateComment_ShouldUpdateContent_WhenAuthorEditsOwnComment() {
        UpdateTicketCommentRequest request =
                new UpdateTicketCommentRequest("Updated content");

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(commentRepository.findByTicketAndTicketCommentNumber(ticket, COMMENT_NUMBER_1))
                .thenReturn(Optional.of(comment1));
        when(authService.currentUser()).thenReturn(author);
        when(commentRepository.save(any(TicketComment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketCommentResponse response =
                ticketCommentService.updateComment(TICKET_ID, COMMENT_NUMBER_1, request);

        assertNotNull(response);
        assertEquals("Updated content", response.content());
        verify(commentRepository, times(1)).save(any(TicketComment.class));
    }

    @Test
    void updateComment_ShouldAllowAdminToEditAnyComment() {
        UpdateTicketCommentRequest request =
                new UpdateTicketCommentRequest("Admin updated");

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(commentRepository.findByTicketAndTicketCommentNumber(ticket, COMMENT_NUMBER_1))
                .thenReturn(Optional.of(comment1));
        when(authService.currentUser()).thenReturn(admin);
        when(commentRepository.save(any(TicketComment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketCommentResponse response =
                ticketCommentService.updateComment(TICKET_ID, COMMENT_NUMBER_1, request);

        assertNotNull(response);
        assertEquals("Admin updated", response.content());
        verify(commentRepository, times(1)).save(any(TicketComment.class));
    }

    @Test
    void updateComment_ShouldThrowAccessDenied_WhenOtherUserEditsForeignComment() {
        UpdateTicketCommentRequest request =
                new UpdateTicketCommentRequest("Should not be saved");

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(commentRepository.findByTicketAndTicketCommentNumber(ticket, COMMENT_NUMBER_1))
                .thenReturn(Optional.of(comment1));
        when(authService.currentUser()).thenReturn(otherUser);

        assertThrows(AccessDeniedException.class,
                () -> ticketCommentService.updateComment(TICKET_ID, COMMENT_NUMBER_1, request));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_ShouldThrowEntityNotFound_WhenCommentNotFound() {
        UpdateTicketCommentRequest request =
                new UpdateTicketCommentRequest("Updated content");

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(commentRepository.findByTicketAndTicketCommentNumber(ticket, COMMENT_NUMBER_1))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> ticketCommentService.updateComment(TICKET_ID, COMMENT_NUMBER_1, request));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_ShouldAllowAuthorToDeleteOwnComment() {
        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(commentRepository.findByTicketAndTicketCommentNumber(ticket, COMMENT_NUMBER_1))
                .thenReturn(Optional.of(comment1));
        when(authService.currentUser()).thenReturn(author);

        assertDoesNotThrow(
                () -> ticketCommentService.deleteComment(TICKET_ID, COMMENT_NUMBER_1));

        verify(commentRepository, times(1)).delete(comment1);
    }

    @Test
    void deleteComment_ShouldAllowAdminToDeleteAnyComment() {
        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(commentRepository.findByTicketAndTicketCommentNumber(ticket, COMMENT_NUMBER_1))
                .thenReturn(Optional.of(comment1));
        when(authService.currentUser()).thenReturn(admin);

        assertDoesNotThrow(
                () -> ticketCommentService.deleteComment(TICKET_ID, COMMENT_NUMBER_1));

        verify(commentRepository, times(1)).delete(comment1);
    }

    @Test
    void deleteComment_ShouldThrowAccessDenied_WhenOtherUserDeletesForeignComment() {
        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(commentRepository.findByTicketAndTicketCommentNumber(ticket, COMMENT_NUMBER_1))
                .thenReturn(Optional.of(comment1));
        when(authService.currentUser()).thenReturn(otherUser);

        assertThrows(AccessDeniedException.class,
                () -> ticketCommentService.deleteComment(TICKET_ID, COMMENT_NUMBER_1));

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_ShouldThrowEntityNotFound_WhenCommentNotFound() {
        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(commentRepository.findByTicketAndTicketCommentNumber(ticket, COMMENT_NUMBER_1))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> ticketCommentService.deleteComment(TICKET_ID, COMMENT_NUMBER_1));

        verify(commentRepository, never()).delete(any());
    }
}
