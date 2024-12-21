package com.nexus.projectstep;

import com.nexus.common.Status;
import com.nexus.employee.Employee;
import com.nexus.employee.EmployeeFinder;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.project.Project;
import com.nexus.project.ProjectFinder;
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
    private final ProjectFinder projectFinder;
    private final EmployeeFinder employeeFinder;

    public ProjectStepController(ProjectStepRepository projectStepRepository, ProjectFinder projectFinder, EmployeeFinder employeeFinder) {
        this.projectStepRepository = projectStepRepository;
        this.projectFinder = projectFinder;
        this.employeeFinder = employeeFinder;
    }

    @GetMapping("by-project/{id}")
    public ResponseEntity<List<ProjectStep>> getAllByProject(@Valid @Positive @PathVariable int id) {
        projectFinder.doesProjectExist(id);

        return ResponseEntity.ok(projectStepRepository.findAllByProjectId(id));
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectStep> getById(@Valid @Positive @PathVariable int id) {
        ProjectStep step = findById(id);

        return ResponseEntity.ok(step);
    }

    @PostMapping
    public ResponseEntity<Integer> create(@Valid @RequestBody CreateProjectStepRequest request) {
        Project project = projectFinder.findById(request.projectId());

        ProjectStep step = new ProjectStep(
                project,
                request.name(),
                request.description(),
                request.startDate(),
                request.expectedEndDate()
        );

        projectStepRepository.save(step);

        return ResponseEntity.created(URI.create("steps/" + step.getId())).body(step.getId());
    }

    @PutMapping
    public void update(@Valid @RequestBody UpdateProjectStepRequest request) {
        ProjectStep step = findById(request.id());

        UpdateHandler.updateEntity(step, tracker -> {
            tracker.updateField(step::getName, request.name(), step::setName);
            tracker.updateField(step::getDescription, request.description(), step::setDescription);
            tracker.updateField(step::getStartDate, request.startDate(), step::setStartDate);
            tracker.updateField(step::getExpectedEndDate, request.expectedEndDate(), step::setExpectedEndDate);
            tracker.updateField(step::getActualEndDate, request.actualEndDate(), step::setActualEndDate);
        }, () -> projectStepRepository.save(step));
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        projectStepRepository.deleteById(id);
    }

    @PatchMapping("{id}/employees/{employeeId}")
    public void addEmployee(@Valid @Positive @PathVariable int id, @Valid @Positive @PathVariable long employeeId) {
        ProjectStep step = findById(id);
        Employee employee = employeeFinder.findById(employeeId);

        step.addEmployee(employee);
        step.getProject().getEmployees().add(employee);

        projectStepRepository.save(step);
    }

    @DeleteMapping("{id}/employees/{employeeId}")
    public void removeEmployee(@Valid @Positive @PathVariable int id, @Valid @Positive @PathVariable long employeeId) {
        ProjectStep step = findById(id);
        Employee employee = employeeFinder.findById(employeeId);

        step.removeEmployee(employee);
        step.getProject().getEmployees().add(employee);

        projectStepRepository.save(step);
    }

    @PatchMapping("{id}/status")
    public void updateStatus(
            @Valid @Positive @PathVariable int id,
            @RequestBody Status status
    ) {
        ProjectStep step = findById(id);
        UpdateHandler.updateEntity(step, tracker -> {
            tracker.updateField(step::getStatus, status, step::setStatus);
        }, () -> projectStepRepository.save(step));
    }

    private ProjectStep findById(int id) {
        return projectStepRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Project step with id " + id + " not found")
                );
    }
}
