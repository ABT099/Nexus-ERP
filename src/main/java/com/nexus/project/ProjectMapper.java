package com.nexus.project;

import com.nexus.employee.BasicEmployeeResponse;
import com.nexus.expense.ExpenseMapper;
import com.nexus.file.FileMapper;
import com.nexus.payment.PaymentMapper;
import com.nexus.projectstep.ProjectStepMapper;
import com.nexus.user.UserMapper;
import org.springframework.stereotype.Component;


@Component
public class ProjectMapper {
    private final UserMapper userMapper;
    private final ExpenseMapper expenseMapper;
    private final PaymentMapper paymentMapper;
    private final ProjectStepMapper projectStepMapper;
    private final FileMapper fileMapper;

    public ProjectMapper(UserMapper userMapper,  ExpenseMapper expenseMapper, PaymentMapper paymentMapper, ProjectStepMapper projectStepMapper, FileMapper fileMapper) {
        this.userMapper = userMapper;
        this.expenseMapper = expenseMapper;
        this.paymentMapper = paymentMapper;
        this.projectStepMapper = projectStepMapper;
        this.fileMapper = fileMapper;
    }

    public BasicProjectResponse toBasicProjectResponse(Project project) {
        return new BasicProjectResponse(
            project.getId(),
            project.getName()
        );
    }

    public ListProjectResponse toListProjectResponse(Project project) {
        return new ListProjectResponse(
                project.getId(),
                project.getName(),
                project.getStartDate(),
                project.getStatus()
        );
    }

    public ProjectResponse toProjectResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getExpectedEndDate(),
                project.getActualEndDate(),
                project.getStatus(),
                userMapper.toUserInfo(project.getOwner()),
                project.getSteps().stream().map(projectStepMapper::toBasicStepResponse).toList(),
                project.getEmployees().stream().map(
                        employee -> new BasicEmployeeResponse(
                                employee.getId(),
                                employee.getUser().getAvatarUrl(),
                                employee.getFirstName(),
                                employee.getLastName(),
                                employee.getEmployeeCode()
                        )
                ).toList(),
                project.getPayments().stream().map(paymentMapper::toBasicPaymentResponse).toList(),
                project.getExpenses().stream().map(expenseMapper::toExpenseResponse).toList(),
                project.getFiles().stream().map(fileMapper::toBasicFileResponse).toList()
        );
    }
}
