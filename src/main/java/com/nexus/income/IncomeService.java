package com.nexus.income;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.project.Project;
import com.nexus.project.ProjectService;
import com.nexus.stripe.StripeService;
import com.nexus.tenant.TenantContext;
import com.nexus.tenant.TenantRepository;
import com.nexus.user.User;
import com.nexus.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncomeService {
    private final UserService userService;
    private final IncomeRepository incomeRepository;
    private final ProjectService projectService;
    private final StripeService stripeService;

    public IncomeService(
            UserService userService,
            IncomeRepository incomeRepository,
            ProjectService projectService,
            StripeService stripeService
    ) {
        this.userService = userService;
        this.incomeRepository = incomeRepository;
        this.projectService = projectService;
        this.stripeService = stripeService;
    }

    public Income findById(int id) {
        return incomeRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Payment not found with id " + id)
                );
    }

    @Transactional
    public Income create(CreateIncomeRequest request) {
        User payer = userService.findById(request.payerId());

        Income income;

        if (request.projectId() != null) {
            Project project = projectService.findById(request.projectId());

            income = new Income(request.amount(), request.currency(), request.paymentDate(), project, payer, TenantContext.getTenantId());
        } else {
            income = new Income(request.amount(), request.currency(), request.paymentDate(), payer, TenantContext.getTenantId());
        }

        if (request.isStripe()) {
            try {
                income = stripeService.createCharge(income);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }

        incomeRepository.save(income);

        return income;
    }
}
