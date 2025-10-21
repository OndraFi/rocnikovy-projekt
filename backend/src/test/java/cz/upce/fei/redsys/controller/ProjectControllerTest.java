package cz.upce.fei.redsys.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.upce.fei.redsys.dto.ProjectDto.CreateProjectRequest;
import cz.upce.fei.redsys.dto.ProjectDto.ProjectResponse;
import cz.upce.fei.redsys.dto.ProjectDto.UpdateProjectRequest;
import cz.upce.fei.redsys.domain.ProjectState;
import cz.upce.fei.redsys.service.ProjectService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import cz.upce.fei.redsys.dto.ProjectDto.PaginatedProjectResponse;
import org.springframework.data.domain.Pageable;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class ProjectControllerTest {

    private static final String API_BASE = "/api/projects";
    private static final Long PROJECT_ID = 5L;
    private static final Long TEST_USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    private ProjectResponse mockProjectResponse;

    @BeforeEach
    void setUp() {
        mockProjectResponse = new ProjectResponse(
                PROJECT_ID,
                "Test Project",
                "A description",
                ProjectState.ACTIVE,
                TEST_USER_ID
        );
    }

    @Test
    @WithMockUser
    void create_ShouldReturnCreatedProjectAnd201() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest(
                "New Project Title",
                "Project description"
        );

        when(projectService.create(any(CreateProjectRequest.class)))
                .thenReturn(mockProjectResponse);

        mockMvc.perform(post(API_BASE)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", API_BASE + "/" + PROJECT_ID))
                .andExpect(jsonPath("$.id").value(PROJECT_ID))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    @WithMockUser
    void listMine_ShouldReturnPaginatedProjectsAnd200() throws Exception {
        ProjectResponse otherProject = new ProjectResponse(6L, "Other Project", "Desc", ProjectState.ACTIVE, TEST_USER_ID);

        PaginatedProjectResponse mockPaginatedResponse = new PaginatedProjectResponse(
                List.of(mockProjectResponse, otherProject),
                0,
                2,
                2L,
                1
        );

        when(projectService.list(any(Pageable.class))).thenReturn(mockPaginatedResponse);

        mockMvc.perform(get(API_BASE)
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects.length()").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.projects[0].name").value("Test Project"));
    }

    @Test
    @WithMockUser
    void get_ShouldReturnProjectAnd200() throws Exception {
        when(projectService.get(PROJECT_ID)).thenReturn(mockProjectResponse);

        mockMvc.perform(get(API_BASE + "/{id}", PROJECT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROJECT_ID))
                .andExpect(jsonPath("$.name").value("Test Project"));
    }

    @Test
    @WithMockUser
    void update_ShouldReturnUpdatedProjectAnd200() throws Exception {
        UpdateProjectRequest request = new UpdateProjectRequest(
                "Updated Name",
                "Updated Description",
                ProjectState.ARCHIVED
        );

        ProjectResponse updatedResponse = new ProjectResponse(
                PROJECT_ID,
                "Updated Name",
                "Updated Description",
                ProjectState.ARCHIVED,
                TEST_USER_ID
        );

        when(projectService.update(eq(PROJECT_ID), any(UpdateProjectRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put(API_BASE + "/{id}", PROJECT_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.state").value("ARCHIVED"));
    }

    @Test
    @WithMockUser
    void create_ShouldReturn400_WhenNameIsMissing() throws Exception {
        CreateProjectRequest invalidRequest = new CreateProjectRequest(
                " ",
                "Valid description"
        );

        mockMvc.perform(post(API_BASE)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("The 'name' field is required"));
    }

    @Test
    @WithMockUser
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(projectService).delete(PROJECT_ID);

        mockMvc.perform(delete(API_BASE + "/{id}", PROJECT_ID)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).delete(PROJECT_ID);
    }
}