package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Project;
import cz.upce.fei.redsys.domain.ProjectState;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.dto.ProjectDto.CreateProjectRequest;
import cz.upce.fei.redsys.dto.ProjectDto.UpdateProjectRequest;
import cz.upce.fei.redsys.dto.ProjectDto.PaginatedProjectResponse;
import cz.upce.fei.redsys.repository.ProjectRepository;
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
public class ProjectServiceTest {

    private static final Long PROJECT_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long CURRENT_USER_ID = 10L;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AuthService authService;

    @Mock
    private Pageable pageable;

    @InjectMocks
    private ProjectService projectService;

    private User currentUser;
    private User otherUser;
    private Project mockProject;

    @BeforeEach
    void setUp() {
        currentUser = User.builder().id(CURRENT_USER_ID).username("current").build();
        otherUser = User.builder().id(OTHER_USER_ID).username("other").build();

        mockProject = Project.builder()
                .id(PROJECT_ID)
                .name("Initial Name")
                .description("Initial Description")
                .state(ProjectState.ACTIVE)
                .owner(currentUser)
                .build();

        when(authService.currentUser()).thenReturn(currentUser);
    }

    @Test
    void create_ShouldSaveNewProjectWithCurrentUserAsOwner() {
        CreateProjectRequest request = new CreateProjectRequest("New Project", "Desc");
        when(projectRepository.save(any(Project.class))).thenReturn(mockProject); // Return a saved project

        projectService.create(request);

        verify(projectRepository, times(1)).save(argThat(project ->
                project.getName().equals("New Project") &&
                        project.getOwner().getId().equals(CURRENT_USER_ID)
        ));
    }

    @Test
    void list_ShouldReturnPaginatedResponse() {
        List<Project> projectList = List.of(mockProject);
        Page<Project> projectPage = new PageImpl<>(projectList, pageable, 1L);

        when(projectRepository.findAllByOwner(currentUser, pageable)).thenReturn(projectPage);

        PaginatedProjectResponse response = projectService.list(pageable);

        assertNotNull(response);
        assertEquals(1, response.totalElements());
        assertEquals(1, response.projects().size());
        assertEquals(mockProject.getName(), response.projects().get(0).name());
        verify(projectRepository, times(1)).findAllByOwner(currentUser, pageable);
    }

    @Test
    void get_ShouldReturnProjectResponse_WhenOwned() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(mockProject));

        projectService.get(PROJECT_ID);

        verify(projectRepository, times(1)).findById(PROJECT_ID);
    }

    @Test
    void get_ShouldThrowNotFound_WhenProjectDoesNotExist() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                projectService.get(PROJECT_ID)
        );
    }

    @Test
    void get_ShouldThrowAccessDenied_WhenProjectIsNotOwned() {
        Project foreignProject = Project.builder().id(PROJECT_ID).owner(otherUser).build();
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(foreignProject));

        assertThrows(AccessDeniedException.class, () ->
                projectService.get(PROJECT_ID)
        );
    }

    @Test
    void update_ShouldUpdateNameDescriptionAndState() {
        UpdateProjectRequest request = new UpdateProjectRequest(
                "New Name",
                "New Description",
                ProjectState.ARCHIVED
        );

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(mockProject));
        when(projectRepository.save(any(Project.class))).thenReturn(mockProject);

        projectService.update(PROJECT_ID, request);

        verify(projectRepository, times(1)).save(argThat(project ->
                project.getName().equals("New Name") &&
                        project.getDescription().equals("New Description") &&
                        project.getState() == ProjectState.ARCHIVED
        ));
    }

    @Test
    void update_ShouldIgnoreNullState() {
        // Arrange
        UpdateProjectRequest request = new UpdateProjectRequest(
                "New Name",
                "New Description",
                null
        );

        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(mockProject));
        when(projectRepository.save(any(Project.class))).thenReturn(mockProject);

        assertEquals(ProjectState.ACTIVE, mockProject.getState());

        projectService.update(PROJECT_ID, request);

        verify(projectRepository, times(1)).save(argThat(project ->
                project.getName().equals("New Name") &&
                        project.getState() == ProjectState.ACTIVE
        ));
    }

    @Test
    void delete_ShouldCallRepositoryDelete_WhenOwned() {
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(mockProject));
        doNothing().when(projectRepository).delete(mockProject);

        projectService.delete(PROJECT_ID);

        verify(projectRepository, times(1)).delete(mockProject);
    }

    @Test
    void delete_ShouldThrowAccessDenied_WhenProjectIsNotOwned() {
        Project foreignProject = Project.builder().id(PROJECT_ID).owner(otherUser).build();
        when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(foreignProject));

        assertThrows(AccessDeniedException.class, () ->
                projectService.delete(PROJECT_ID)
        );
        verify(projectRepository, never()).delete(any());
    }
}