package com.nexus.project;

import com.nexus.abstraction.UserContext;
import com.nexus.common.ArchivableQueryType;
import com.nexus.common.ArchivedService;
import com.nexus.common.Status;
import com.nexus.exception.NoUpdateException;
import com.nexus.file.File;
import com.nexus.file.FileService;
import com.nexus.monitor.ActionType;
import com.nexus.monitor.MonitorManager;
import com.nexus.tenant.TenantContext;
import com.nexus.user.User;
import com.nexus.user.UserService;
import com.nexus.utils.UpdateHandler;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("projects")
public class ProjectController extends UserContext {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final FileService fileService;
    private final ProjectMapper projectMapper;
    private final MonitorManager monitorManager;

    public ProjectController(
            ProjectRepository projectRepository,
            ProjectService projectService,
            UserService userService,
            FileService fileService,
            ProjectMapper projectMapper,
            MonitorManager monitorManager
    ) {
        this.projectRepository = projectRepository;
        this.projectService = projectService;
        this.userService = userService;
        this.fileService = fileService;
        this.projectMapper = projectMapper;
        this.monitorManager = monitorManager;
    }

    @GetMapping
    public ResponseEntity<List<ListProjectResponse>> getAll(
            @RequestParam(
                    required = false,
                    name = "a"
            ) ArchivableQueryType archived
    ) {
        List<Project> projects = ArchivedService.determine(archived, projectRepository);

        return ResponseEntity.ok(
                projects.stream()
                        .map(projectMapper::toListProjectResponse)
                        .toList()
        );
    }

    @GetMapping("by-owner/{id}")
    public ResponseEntity<List<ListProjectResponse>> getAllByOwner(@Valid @Positive @PathVariable long id) {
        userService.doesUserExists(id);

        return ResponseEntity.ok(
                projectRepository.findAllByOwnerId(id).stream()
                    .map(projectMapper::toListProjectResponse)
                    .toList()
        );
    }

    @GetMapping("me")
    public ResponseEntity<List<ListProjectResponse>> getMyProjects() {
        long userId = getUserId();

        userService.doesUserExists(userId);

        return ResponseEntity.ok(
                projectRepository.findAllByOwnerId(userId).stream()
                    .map(projectMapper::toListProjectResponse)
                    .toList()
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectResponse> getById(@Valid @Positive @PathVariable int id) {
        Project project = projectService.findById(id);

        return ResponseEntity.ok(projectMapper.toProjectResponse(project));
    }

    @PostMapping
    public ResponseEntity<Integer> create(@Valid @RequestBody CreateProjectRequest request) {
        User owner = userService.findById(request.ownerId());

        Project project = new Project(
                owner,
                request.price(),
                request.name(),
                request.description(),
                request.startDate(),
                request.expectedEndDate(),
                TenantContext.getTenantId()
        );

        projectRepository.save(project);

        monitorManager.monitor(project, ActionType.CREATE);

        return ResponseEntity.created(URI.create("/projects/" + project.getId())).body(project.getId());
    }

    @PutMapping
    public void update(@Valid @RequestBody UpdateProjectRequest request) {
        Project project = projectService.findById(request.id());

        if (project.isArchived()) {
            throw new NoUpdateException("Archived project cannot be updated");
        }

        UpdateHandler.updateEntity(project, tracker -> {
            tracker.updateField(project::getPrice, request.price(), project::setPrice);
            tracker.updateField(project::getName, request.name(), project::setName);
            tracker.updateField(project::getDescription, request.description(), project::setDescription);
            tracker.updateField(project::getStartDate, request.startDate(), project::setStartDate);
            tracker.updateField(project::getExpectedEndDate, request.expectedEndDate(), project::setExpectedEndDate);
            tracker.updateField(project::getActualEndDate, request.actualEndDate(), project::setActualEndDate);
        }, () -> projectRepository.save(project), monitorManager);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        Project project = projectService.findById(id);

        projectRepository.delete(project);

        monitorManager.monitor(project, ActionType.DELETE);
    }

    @PatchMapping("{id}/status")
    public void updateStatus(@Valid @Positive @PathVariable int id, @RequestBody Status status) {
        Project project = projectService.findById(id);

        if (project.isArchived()) {
            throw new NoUpdateException("Archived project cannot be updated");
        }

        UpdateHandler.updateEntity(project,tracker ->
                tracker.updateField(project::getStatus, status, project::setStatus),
                () -> projectRepository.save(project), monitorManager
        );
    }

    @PatchMapping("{id}/files/{fileId}")
    public void addFile(@Valid @Positive @PathVariable int id, @Valid @Positive @PathVariable int fileId) {
        Project project = projectService.findById(id);
        File file = fileService.findById(fileId);

        project.addFile(file);

        projectRepository.save(project);

        monitorManager.monitor(project, ActionType.ADD_FILE, file.getName());
    }

    @DeleteMapping("{id}/files/{fileId}")
    public void removeFile(@Valid @Positive @PathVariable int id, @Valid @Positive @PathVariable int fileId) {
        Project project = projectService.findById(id);
        File file = fileService.findById(fileId);

        project.removeFile(file);

        projectRepository.save(project);

        monitorManager.monitor(project, ActionType.REMOVE_FILE, file.getName());
    }

    @PatchMapping("archive/{id}")
    public void archive(@Valid @Positive @PathVariable int id) {
        Project project = projectService.findById(id);

        if (project.isArchived()) {
            throw new NoUpdateException("Project is already archived");
        }

        for (File file : project.getFiles()) {
            file.getProjects().remove(project);
        }

        project.setArchived(true);

        projectRepository.save(project);

        monitorManager.monitor(project, ActionType.ARCHIVE);
    }
}
