package com.nexus.projectstep;

import com.nexus.common.Status;
import com.nexus.employee.BasicEmployeeResponse;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ProjectStepMapper {
    public BasicStepResponse toBasicStepResponse(ProjectStep projectStep) {
        ZonedDateTime endDate;

        if (projectStep.getStatus().equals(Status.COMPLETED) || projectStep.getStatus().equals(Status.CANCELLED)) {
            endDate = projectStep.getActualEndDate();
        } else {
            endDate = projectStep.getExpectedEndDate();
        }


        return new BasicStepResponse(
                projectStep.getId(),
                projectStep.getName(),
                projectStep.getDescription(),
                projectStep.getStartDate(),
                endDate,
                projectStep.getStatus()
        );
    }

    public StepResponse toStepResponse(ProjectStep projectStep) {
        return new StepResponse(
                projectStep.getId(),
                projectStep.getName(),
                projectStep.getDescription(),
                projectStep.getStartDate(),
                projectStep.getExpectedEndDate(),
                projectStep.getActualEndDate(),
                projectStep.getStatus(),
                projectStep.getEmployees().stream().map(
                        employee -> new BasicEmployeeResponse(
                                employee.getId(),
                                employee.getUser().getAvatarUrl(),
                                employee.getFirstName(),
                                employee.getLastName(),
                                employee.getEmployeeCode())
                ).toList()
        );
    }
}
