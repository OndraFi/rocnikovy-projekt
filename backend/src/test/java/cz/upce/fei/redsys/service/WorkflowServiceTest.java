package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.*;
import cz.upce.fei.redsys.dto.TicketCommentDto.CreateTicketCommentRequest;
import cz.upce.fei.redsys.dto.TicketDto.TicketResponse;
import cz.upce.fei.redsys.repository.ArticleRepository;
import cz.upce.fei.redsys.repository.TicketRepository;
import cz.upce.fei.redsys.service.WorkflowService.WorkflowException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkflowServiceTest {

    private static final Long TICKET_ID = 1L;
    private static final Long ARTICLE_ID = 10L;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private AuthService authService;

    @Mock
    private ArticleVersionService articleVersionService;

    @Mock
    private TicketService ticketService;

    @Mock
    private TicketCommentService ticketCommentService;

    @InjectMocks
    private WorkflowService workflowService;

    private Ticket ticket;
    private Article article;
    private User editor;
    private User chiefEditor;
    private User admin;
    private User reviewer;
    private User otherEditor;

    @BeforeEach
    void setUp() {
        article = Article.builder()
                .id(ARTICLE_ID)
                .title("Test article")
                .articleState(ArticleState.DRAFT)
                .build();

        editor = User.builder()
                .id(1L)
                .username("editor")
                .role(UserRole.EDITOR)
                .build();

        chiefEditor = User.builder()
                .id(2L)
                .username("chief")
                .role(UserRole.CHIEF_EDITOR)
                .build();

        admin = User.builder()
                .id(3L)
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        reviewer = User.builder()
                .id(4L)
                .username("reviewer")
                .role(UserRole.REVIEWER)
                .build();

        otherEditor = User.builder()
                .id(5L)
                .username("otherEditor")
                .role(UserRole.EDITOR)
                .build();

        ticket = new Ticket();
        ticket.setId(TICKET_ID);
        ticket.setArticle(article);
        ticket.setState(TicketState.OPEN);
        ticket.setAssignee(null);
    }

    @Test
    void transition_ShouldReturnSameResponse_WhenStateUnchanged() {
        ticket.setState(TicketState.OPEN);

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(authService.currentUser()).thenReturn(chiefEditor);

        TicketResponse response =
                workflowService.transition(TICKET_ID, TicketState.OPEN, null);

        assertNotNull(response);
        assertEquals(TicketState.OPEN, response.state());
        verify(ticketRepository, never()).save(any());
        verify(ticketCommentService, never()).createComment(anyLong(), any());
    }

    @Test
    void transition_OpenToInProgress_ShouldAssignEditorAndSaveTicket() {
        ticket.setState(TicketState.OPEN);
        ticket.setAssignee(editor);

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(authService.currentUser()).thenReturn(editor);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponse response =
                workflowService.transition(TICKET_ID, TicketState.IN_PROGRESS, null);

        assertNotNull(response);
        assertEquals(TicketState.IN_PROGRESS, response.state());
        assertNotNull(ticket.getAssignee());
        assertEquals(editor.getId(), ticket.getAssignee().getId());
        verify(ticketRepository, times(1)).save(ticket);
        verify(articleRepository, never()).save(any());
    }

    @Test
    void transition_OpenToInProgress_ShouldThrowAccessDenied_WhenUserNotAllowedRole() {
        ticket.setState(TicketState.OPEN);

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        User normalUser = User.builder()
                .id(10L)
                .username("user")
                .role(UserRole.USER)
                .build();
        when(authService.currentUser()).thenReturn(normalUser);

        assertThrows(AccessDeniedException.class,
                () -> workflowService.transition(TICKET_ID, TicketState.IN_PROGRESS, null));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void transition_OpenToInProgress_ShouldThrowAccessDenied_WhenEditorNotAssignee() {
        ticket.setState(TicketState.OPEN);
        ticket.setAssignee(otherEditor);

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(authService.currentUser()).thenReturn(editor);

        assertThrows(AccessDeniedException.class,
                () -> workflowService.transition(TICKET_ID, TicketState.IN_PROGRESS, null));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void transition_InProgressToForReview_ShouldSetArticleInReview_CreateVersion_AndSave() {
        ticket.setState(TicketState.IN_PROGRESS);
        ticket.setAssignee(editor);
        article.setArticleState(ArticleState.DRAFT);

        ArticleVersion latestVersion = ArticleVersion.builder()
                .id(100L)
                .article(article)
                .versionNumber(1)
                .content("Current content")
                .build();

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(authService.currentUser()).thenReturn(editor);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(articleVersionService.getLatestVersion(article)).thenReturn(latestVersion);
        when(articleVersionService.createNewVersionIfChanged(article, "Current content"))
                .thenReturn(latestVersion);

        TicketResponse response =
                workflowService.transition(TICKET_ID, TicketState.FOR_REVIEW, "Ready for review");

        assertNotNull(response);
        assertEquals(TicketState.FOR_REVIEW, response.state());
        assertEquals(ArticleState.IN_REVIEW, article.getArticleState());
        verify(articleRepository, times(1)).save(article);
        verify(articleVersionService, times(1)).getLatestVersion(article);
        verify(articleVersionService, times(1))
                .createNewVersionIfChanged(article, "Current content");
        verify(ticketRepository, times(1)).save(ticket);
        verify(ticketCommentService, times(1))
                .createComment(eq(TICKET_ID), any(CreateTicketCommentRequest.class));
    }

    @Test
    void transition_ForReviewToInProgress_ShouldSetArticleDraft() {
        ticket.setState(TicketState.FOR_REVIEW);
        ticket.setAssignee(editor);
        article.setArticleState(ArticleState.IN_REVIEW);

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(authService.currentUser()).thenReturn(editor);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponse response =
                workflowService.transition(TICKET_ID, TicketState.IN_PROGRESS, null);

        assertNotNull(response);
        assertEquals(TicketState.IN_PROGRESS, response.state());
        assertEquals(ArticleState.DRAFT, article.getArticleState());
        verify(articleRepository, times(1)).save(article);
        verify(articleVersionService, never()).createNewVersionIfChanged(any(), anyString());
    }

    @Test
    void transition_ApprovedToPublished_ShouldSetArticlePublishedAndCreateVersion() {
        ticket.setState(TicketState.APPROVED);
        article.setArticleState(ArticleState.IN_REVIEW);

        ArticleVersion latestVersion = ArticleVersion.builder()
                .id(100L)
                .article(article)
                .versionNumber(1)
                .content("Approved content")
                .build();

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(authService.currentUser()).thenReturn(chiefEditor);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(articleVersionService.getLatestVersion(article)).thenReturn(latestVersion);
        when(articleVersionService.createNewVersionIfChanged(article, "Approved content"))
                .thenReturn(latestVersion);

        TicketResponse response =
                workflowService.transition(TICKET_ID, TicketState.PUBLISHED, null);

        assertNotNull(response);
        assertEquals(TicketState.PUBLISHED, response.state());
        assertEquals(ArticleState.PUBLISHED, article.getArticleState());
        assertNotNull(article.getPublishedAt());
        verify(articleRepository, times(1)).save(article);
        verify(articleVersionService, times(1)).getLatestVersion(article);
        verify(articleVersionService, times(1))
                .createNewVersionIfChanged(article, "Approved content");
    }

    @Test
    void transition_ShouldThrowWorkflowException_WhenInvalidTransition() {
        ticket.setState(TicketState.OPEN);

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(authService.currentUser()).thenReturn(chiefEditor);

        assertThrows(WorkflowException.class,
                () -> workflowService.transition(TICKET_ID, TicketState.PUBLISHED, null));

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void transition_ShouldAllowAdminForAnyTargetState_IgnoringRoleRestrictions() {
        ticket.setState(TicketState.OPEN);

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(authService.currentUser()).thenReturn(admin);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TicketResponse response =
                workflowService.transition(TICKET_ID, TicketState.IN_PROGRESS, null);

        assertNotNull(response);
        assertEquals(TicketState.IN_PROGRESS, response.state());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void transition_ShouldThrowAccessDenied_ForReviewerOnInvalidOwnership() {
        ticket.setState(TicketState.OPEN);

        when(ticketService.requireTicketById(TICKET_ID)).thenReturn(ticket);
        when(authService.currentUser()).thenReturn(reviewer);

        assertThrows(AccessDeniedException.class,
                () -> workflowService.transition(TICKET_ID, TicketState.IN_PROGRESS, null));

        verify(ticketRepository, never()).save(any());
    }
}
