package cz.upce.fei.redsys.service;

import cz.upce.fei.redsys.domain.Project;
import cz.upce.fei.redsys.domain.ProjectState;
import cz.upce.fei.redsys.domain.User;
import cz.upce.fei.redsys.dto.ProjectDto;
import cz.upce.fei.redsys.dto.ProjectDto.CreateProjectRequest;
import cz.upce.fei.redsys.dto.ProjectDto.ProjectResponse;
import cz.upce.fei.redsys.dto.ProjectDto.PaginatedProjectResponse;
import cz.upce.fei.redsys.dto.ProjectDto.UpdateProjectRequest;
import cz.upce.fei.redsys.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final AuthService authService;

    @Transactional
    public ProjectResponse create(CreateProjectRequest req) {
        User owner = authService.currentUser();
        Project project = Project.builder()
                .name(req.name())
                .description(req.description())
                .state(ProjectState.ACTIVE)
                .owner(owner)
                .build();

        return ProjectDto.toProjectResponse(projectRepository.save(project));
    }

    public PaginatedProjectResponse list(Pageable pageable) {
        User owner = authService.currentUser();
        Page<Project> projectPage = projectRepository.findAllByOwner(owner, pageable);

        List<ProjectDto.ProjectResponse> projectResponses = projectPage.getContent().stream()
                .map(ProjectDto::toProjectResponse)
                .toList();

        return new PaginatedProjectResponse(
                projectResponses,
                projectPage.getNumber(),
                projectPage.getSize(),
                projectPage.getTotalElements(),
                projectPage.getTotalPages()
        );
    }

    public ProjectResponse get(Long id) {
        return ProjectDto.toProjectResponse(findOwnedProject(id));
    }

    @Transactional
    public ProjectResponse update(Long id, UpdateProjectRequest req) {
        Project project = findOwnedProject(id);
        project.setName(req.name());
        project.setDescription(req.description());

        if (req.state() != null) {
            project.setState(req.state());
        }

        return ProjectDto.toProjectResponse(projectRepository.save(project));
    }

    @Transactional
    public void delete(Long id) {
        Project project = findOwnedProject(id);
        projectRepository.delete(project);
    }

    public Project getOwnedProjectEntity(Long id) {
        return findOwnedProject(id);
    }

    private Project findOwnedProject(Long id) {
        User owner = authService.currentUser();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        if (!project.getOwner().getId().equals(owner.getId())) {
            throw new AccessDeniedException("You do not have permission to access this project");
        }
        return project;
    }
}