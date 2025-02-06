package com.nexus.income;

import com.nexus.common.ArchivableQueryType;
import com.nexus.common.ArchivedService;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.monitor.ActionType;
import com.nexus.monitor.MonitorManager;
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
    private final IncomeMapper incomeMapper;
    private final MonitorManager monitorManager;
    private final IncomeService incomeService;

    public IncomeController(
            IncomeRepository incomeRepository,
            IncomeMapper incomeMapper,
            MonitorManager monitorManager, IncomeService incomeService
    ) {
        this.incomeRepository = incomeRepository;
        this.incomeMapper = incomeMapper;
        this.monitorManager = monitorManager;
        this.incomeService = incomeService;
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
        Income income = incomeService.findById(id);

        return ResponseEntity.ok(incomeMapper.toIncomeResponse(income));
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CreateIncomeRequest request) {
        Income income = incomeService.create(request);

        monitorManager.monitor(income, ActionType.CREATE);

        return ResponseEntity.created(URI.create("/payments/" + income.getId())).body(income.getId());
    }

    @PutMapping("{id}")
    public void update(@Valid @Positive @PathVariable int id, @Valid @RequestBody UpdateIncomeRequest request) {
        Income income = incomeService.findById(id);

        UpdateHandler.updateEntity(income, tracker -> {
            tracker.updateField(income::getAmount, request.amount(), income::setAmount);
            tracker.updateField(income::getPaymentDate, request.paymentDate(), income::setPaymentDate);
        }, () -> incomeRepository.save(income), monitorManager);
    }

    @PatchMapping("archive/{id}")
    public void archive(@Valid @Positive @PathVariable int id) {
        Income income = incomeService.findById(id);

        incomeRepository.archiveById(id);

        monitorManager.monitor(income, ActionType.ARCHIVE);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        Income income = incomeService.findById(id);

        incomeRepository.delete(income);

        monitorManager.monitor(income, ActionType.DELETE);
    }
}
