package com.nexus.projectstep;

import com.nexus.common.Status;
import com.nexus.employee.BasicEmployeeResponse;
import com.nexus.ineteraction.ListInteractionResponse;

import java.time.Instant;
import java.util.List;

public record StepResponse(
        Integer id,
        String name,
        String description,
        Instant startDate,
        Instant expectedEndDate,
        Instant actualEndDate,
        Status status,
        List<BasicEmployeeResponse> employees,
        List<ListInteractionResponse> interactions
) { }
