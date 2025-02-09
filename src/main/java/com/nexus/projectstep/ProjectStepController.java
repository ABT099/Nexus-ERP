package com.nexus.projectstep;

import com.nexus.common.ArchivableQueryType;
import com.nexus.common.ArchivedService;
import com.nexus.common.Status;
import com.nexus.employee.Employee;
import com.nexus.employee.EmployeeService;
import com.nexus.exception.NoUpdateException;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.monitor.ActionType;
import com.nexus.monitor.MonitorManager;
import com.nexus.project.Project;
import com.nexus.project.ProjectService;
import com.nexus.utils.UpdateHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("steps")
public class ProjectStepController {

    private final ProjectStepRepository projectStepRepository;
    private final ProjectService projectService;
    private final EmployeeService employeeService;
    private final ProjectStepMapper projectStepMapper;
    private final MonitorManager monitorManager;

    public ProjectStepController(
            ProjectStepRepository projectStepRepository,
            ProjectService projectService,
            EmployeeService employeeService,
            ProjectStepMapper projectStepMapper,
            MonitorManager monitorManager
    ) {
        this.projectStepRepository = projectStepRepository;
        this.projectService = projectService;
        this.employeeService = employeeService;
        this.projectStepMapper = projectStepMapper;
        this.monitorManager = monitorManager;
    }

    @GetMapping("by-project/{id}")
    public ResponseEntity<List<BasicStepResponse>> getAllByProject(
            @Valid @Positive @PathVariable int id,
            @RequestParam(
                    required = false,
                    name = "a"
            ) ArchivableQueryType archived
    ) {
        projectService.doesProjectExist(id);

        List<ProjectStep> steps = ArchivedService.determine(archived, projectStepRepository);

        return ResponseEntity.ok(
                steps.stream()
                        .map(projectStepMapper::toBasicStepResponse)
                        .toList()
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<StepResponse> getById(@Valid @Positive @PathVariable int id) {
        ProjectStep step = findById(id);

        return ResponseEntity.ok(projectStepMapper.toStepResponse(step));
    }

    @PostMapping
    public ResponseEntity<Integer> create(@Valid @RequestBody CreateProjectStepRequest request) {
        Project project = projectService.findById(request.projectId());

        if (project.isArchived()) {
            throw new NoUpdateException("Project is archived");
        }

        ProjectStep step = new ProjectStep(
                project,
                request.name(),
                request.description(),
                request.startDate(),
                request.expectedEndDate()
        );

        projectStepRepository.save(step);

        monitorManager.monitor(step, ActionType.CREATE);

        return ResponseEntity.created(URI.create("steps/" + step.getId())).body(step.getId());
    }

    @PutMapping
    public void update(@Valid @RequestBody UpdateProjectStepRequest request) {
        ProjectStep step = findById(request.id());

        if (step.isArchived()) {
            throw new NoUpdateException("Step is archived");
        }

        UpdateHandler.updateEntity(step, tracker -> {
            tracker.updateField(step::getName, request.name(), step::setName);
            tracker.updateField(step::getDescription, request.description(), step::setDescription);
            tracker.updateField(step::getStartDate, request.startDate(), step::setStartDate);
            tracker.updateField(step::getExpectedEndDate, request.expectedEndDate(), step::setExpectedEndDate);
            tracker.updateField(step::getActualEndDate, request.actualEndDate(), step::setActualEndDate);
        }, () -> projectStepRepository.save(step), monitorManager);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        ProjectStep step = findById(id);

        projectStepRepository.delete(step);

        monitorManager.monitor(step, ActionType.DELETE);
    }

    @PatchMapping("{id}/employees/{employeeId}")
    public void addEmployee(@Valid @Positive @PathVariable int id, @Valid @Positive @PathVariable long employeeId) {
        ProjectStep step = findById(id);
        Employee employee = employeeService.findById(employeeId);

        step.addEmployee(employee);
        step.getProject().getEmployees().add(employee);

        projectStepRepository.save(step);

        monitorManager.monitor(step, ActionType.ADD_EMPLOYEE);
    }

    @DeleteMapping("{id}/employees/{employeeId}")
    public void removeEmployee(@Valid @Positive @PathVariable int id, @Valid @Positive @PathVariable long employeeId) {
        ProjectStep step = findById(id);
        Employee employee = employeeService.findById(employeeId);

        step.removeEmployee(employee);
        step.getProject().getEmployees().add(employee);

        projectStepRepository.save(step);

        monitorManager.monitor(step, ActionType.REMOVE_EMPLOYEE);
    }

    @PatchMapping("{id}/status")
    public void updateStatus(
            @Valid @Positive @PathVariable int id,
            @RequestBody Status status
    ) {
        ProjectStep step = findById(id);
        UpdateHandler.updateEntity(step, tracker -> {
            tracker.updateField(step::getStatus, status, step::setStatus);
        }, () -> projectStepRepository.save(step), monitorManager);
    }

    @PatchMapping("archive/{id}")
    public void archive(@Valid @Positive @PathVariable int id) {
        ProjectStep step = findById(id);

        if (step.isArchived()) {
            throw new NoUpdateException("Step is already archived");
        }

        step.setArchived(true);

        projectStepRepository.save(step);

        monitorManager.monitor(step, ActionType.ARCHIVE);
    }

    private ProjectStep findById(int id) {
        return projectStepRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Project step with id " + id + " not found")
                );
    }
}
