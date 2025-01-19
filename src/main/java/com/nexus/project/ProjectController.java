package com.nexus.project;

import com.nexus.abstraction.UserContext;
import com.nexus.common.Status;
import com.nexus.file.File;
import com.nexus.file.FileService;
import com.nexus.user.User;
import com.nexus.user.UserService;
import com.nexus.utils.UpdateHandler;
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
    private final ProjectFinder projectFinder;
    private final UserService userService;
    private final FileService fileService;
    private final ProjectMapper projectMapper;

    public ProjectController(ProjectRepository projectRepository, ProjectFinder projectFinder, UserService userService, FileService fileService, ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.projectFinder = projectFinder;
        this.userService = userService;
        this.fileService = fileService;
        this.projectMapper = projectMapper;
    }

    @GetMapping
    public ResponseEntity<List<ListProjectResponse>> getAll() {
        return ResponseEntity.ok(
                projectRepository.findAll().stream()
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
        Project project = projectFinder.findById(id);

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
                request.expectedEndDate()
        );

        projectRepository.save(project);

        return ResponseEntity.created(URI.create("/projects/" + project.getId())).body(project.getId());
    }

    @PutMapping
    public void update(@Valid @RequestBody UpdateProjectRequest request) {
        Project project = projectFinder.findById(request.id());

        UpdateHandler.updateEntity(tracker -> {
            tracker.updateField(project::getPrice, request.price(), project::setPrice);
            tracker.updateField(project::getName, request.name(), project::setName);
            tracker.updateField(project::getDescription, request.description(), project::setDescription);
            tracker.updateField(project::getStartDate, request.startDate(), project::setStartDate);
            tracker.updateField(project::getExpectedEndDate, request.expectedEndDate(), project::setExpectedEndDate);
            tracker.updateField(project::getActualEndDate, request.actualEndDate(), project::setActualEndDate);
        }, () -> projectRepository.save(project));
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        projectRepository.deleteById(id);
    }

    @PatchMapping("{id}/status")
    public void updateStatus(@Valid @Positive @PathVariable int id, Status status) {
        Project project = projectFinder.findById(id);

        UpdateHandler.updateEntity(tracker -> {
            tracker.updateField(project::getStatus, status, project::setStatus);
        }, () -> projectRepository.save(project));
    }

    @PatchMapping("{id}/files/{fileId}")
    public void addFile(@Valid @Positive @PathVariable int id, @Valid @Positive @PathVariable int fileId) {
        Project project = projectFinder.findById(id);
        File file = fileService.findById(fileId);

        project.addFile(file);

        projectRepository.save(project);
    }

    @DeleteMapping("{id}/files/{fileId}")
    public void removeFile(@Valid @Positive @PathVariable int id, @Valid @Positive @PathVariable int fileId) {
        Project project = projectFinder.findById(id);
        File file = fileService.findById(fileId);

        project.removeFile(file);

        projectRepository.save(project);
    }
}
