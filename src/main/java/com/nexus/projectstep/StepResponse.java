package com.nexus.projectstep;

import com.nexus.common.Status;
import com.nexus.employee.BasicEmployeeResponse;
import com.nexus.ineteraction.ListInteractionResponse;

import java.time.ZonedDateTime;
import java.util.List;

public record StepResponse(
        Integer id,
        String name,
        String description,
        ZonedDateTime startDate,
        ZonedDateTime expectedEndDate,
        ZonedDateTime actualEndDate,
        Status status,
        List<BasicEmployeeResponse> employees,
        List<ListInteractionResponse> interactions
) { }
