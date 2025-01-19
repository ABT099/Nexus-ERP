package com.nexus.project;

import com.nexus.common.Status;
import com.nexus.employee.BasicEmployeeResponse;
import com.nexus.expense.ExpenseResponse;
import com.nexus.file.BasicFileResponse;
import com.nexus.payment.BasicPaymentResponse;
import com.nexus.projectstep.BasicStepResponse;
import com.nexus.user.UserInfoDTO;

import java.time.ZonedDateTime;
import java.util.List;

public record ProjectResponse(
        Integer id,
        String name,
        String description,
        ZonedDateTime startDate,
        ZonedDateTime expectedEndDate,
        ZonedDateTime actualEndDate,
        Status status,
        UserInfoDTO owner,
        List<BasicStepResponse> steps,
        List<BasicEmployeeResponse> employees,
        List<BasicPaymentResponse> payments,
        List<ExpenseResponse> expenses,
        List<BasicFileResponse> files
) { }
