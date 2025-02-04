package com.nexus.income;

import com.nexus.common.ArchivableQueryType;
import com.nexus.common.ArchivedService;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.monitor.ActionType;
import com.nexus.monitor.MonitorManager;
import com.nexus.project.Project;
import com.nexus.project.ProjectFinder;
import com.nexus.tenant.TenantContext;
import com.nexus.user.User;
import com.nexus.user.UserService;
import com.nexus.utils.UpdateHandler;
import com.nexus.zoned.Zoned;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("payments")
public class IncomeController {

    private final IncomeRepository incomeRepository;
    private final ProjectFinder projectFinder;
    private final UserService userService;
    private final IncomeMapper incomeMapper;
    private final MonitorManager monitorManager;

    public IncomeController(
            IncomeRepository incomeRepository,
            ProjectFinder projectFinder,
            UserService userService,
            IncomeMapper incomeMapper,
            MonitorManager monitorManager
    ) {
        this.incomeRepository = incomeRepository;
        this.projectFinder = projectFinder;
        this.userService = userService;
        this.incomeMapper = incomeMapper;
        this.monitorManager = monitorManager;
    }

    @Zoned
    @GetMapping
    public ResponseEntity<List<BasicIncomeResponse>> getAll(
            @RequestParam(
                    required = false,
                    name = "a"
            ) ArchivableQueryType archived
    ) {
        List<Income> incomes = ArchivedService.determine(archived, incomeRepository);

        return ResponseEntity.ok(incomes.stream()
                .map(incomeMapper::toBasicIncomeResponse)
                .toList());
    }

    @Zoned
    @GetMapping("{id}")
    public ResponseEntity<IncomeResponse> getById(@Valid @Positive @PathVariable("id") int id) {
        Income income = findById(id);

        return ResponseEntity.ok(incomeMapper.toIncomeResponse(income));
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CreateIncomeRequest request) {
        User payer = userService.findById(request.payerId());

        Income income;

        if (request.projectId() != null) {
            Project project = projectFinder.findById(request.projectId());

            income = new Income(request.amount(), request.paymentDate(), project, payer, TenantContext.getTenantId());
        } else {
            income = new Income(request.amount(), request.paymentDate(), payer, TenantContext.getTenantId());
        }

        incomeRepository.save(income);

        monitorManager.monitor(income, ActionType.CREATE);

        return ResponseEntity.created(URI.create("/payments/" + income.getId())).body(income.getId());
    }

    @PutMapping("{id}")
    public void update(@Valid @Positive @PathVariable int id, @Valid @RequestBody UpdateIncomeRequest request) {
        Income income = findById(id);

        UpdateHandler.updateEntity(income, tracker -> {
            tracker.updateField(income::getAmount, request.amount(), income::setAmount);
            tracker.updateField(income::getPaymentDate, request.paymentDate(), income::setPaymentDate);
        }, () -> incomeRepository.save(income), monitorManager);
    }

    @PatchMapping("archive/{id}")
    public void archive(@Valid @Positive @PathVariable int id) {
        Income income = findById(id);

        incomeRepository.archiveById(id);

        monitorManager.monitor(income, ActionType.ARCHIVE);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        Income income = findById(id);

        incomeRepository.delete(income);

        monitorManager.monitor(income, ActionType.DELETE);
    }

    private Income findById(int id) {
        return incomeRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Payment not found with id " + id)
                );
    }
}
