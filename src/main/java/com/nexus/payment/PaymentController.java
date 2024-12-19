package com.nexus.payment;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.project.Project;
import com.nexus.project.ProjectFinder;
import com.nexus.user.User;
import com.nexus.user.UserService;
import com.nexus.utils.UpdateHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("payments")
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final ProjectFinder projectFinder;
    private final UserService userService;

    public PaymentController(PaymentRepository paymentRepository, ProjectFinder projectFinder, UserService userService) {
        this.paymentRepository = paymentRepository;
        this.projectFinder = projectFinder;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAll() {
        return ResponseEntity.ok(paymentRepository.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Payment> getById(@Valid @Positive @PathVariable("id") int id) {
        return ResponseEntity.ok(findById(id));
    }

    @PostMapping
    public ResponseEntity<Integer> create(@Valid @RequestBody CreatePaymentRequest request) {
        User payer = userService.findById(request.payerId());

        Payment payment;

        if (request.projectId() != null) {
            Project project = projectFinder.findById(request.projectId());

            payment = new Payment(request.amount(), request.paymentDate(), project, payer);
        } else {
            payment = new Payment(request.amount(), request.paymentDate(), payer);
        }

        paymentRepository.save(payment);

        return ResponseEntity.created(URI.create("/payments/" + payment.getId())).body(payment.getId());
    }

    @PutMapping("{id}")
    public void update(@Valid @Positive @PathVariable int id, @Valid @RequestBody UpdatePaymentRequest request) {
        Payment payment = findById(id);

        UpdateHandler.updateEntity(payment, tracker -> {
            tracker.updateField(payment::getAmount, request.amount(), payment::setAmount);
            tracker.updateField(payment::getPaymentDate, request.paymentDate(), payment::setPaymentDate);
        }, () -> paymentRepository.save(payment));
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable int id) {
        paymentRepository.deleteById(id);
    }

    private Payment findById(int id) {
        return paymentRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Payment not found with id " + id)
                );
    }
}
