package com.nexus.income;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.project.Project;
import com.nexus.project.ProjectService;
import com.nexus.tenant.TenantContext;
import com.nexus.user.User;
import com.nexus.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class IncomeService {
    private final UserService userService;
    private final IncomeRepository incomeRepository;
    private final ProjectService projectService;

    public IncomeService(
            UserService userService,
            IncomeRepository incomeRepository,
            ProjectService projectService
    ) {
        this.userService = userService;
        this.incomeRepository = incomeRepository;
        this.projectService = projectService;
    }

    public Income findById(int id) {
        return incomeRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Payment not found with id " + id)
                );
    }

    public Income create(CreateIncomeRequest request) {
        User payer = userService.findById(request.payerId());

        Income income;

        if (request.projectId() != null) {
            Project project = projectService.findById(request.projectId());

            income = new Income(request.amount(), request.paymentDate(), project, payer, TenantContext.getTenantId());
        } else {
            income = new Income(request.amount(), request.paymentDate(), payer, TenantContext.getTenantId());
        }

        incomeRepository.save(income);

        return income;
    }
}
