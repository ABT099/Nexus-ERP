package com.nexus.customer;

import com.nexus.common.ArchivableQueryType;
import com.nexus.person.CreatePersonRequest;
import com.nexus.person.UpdatePersonRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getCustomers(
            @RequestParam(
                    required = false,
                    name = "a"
            )ArchivableQueryType queryType) {
        List<Customer> customers;

        switch (queryType) {
            case ALL -> customers = customerService.findAll();
            case Archived -> customers = customerService.findAllArchived();
            default -> customers = customerService.findAllNonArchived();
        }

        return ResponseEntity.ok(customers);
    }

    @GetMapping("{id}")
    public ResponseEntity<Customer> getById(
            @Valid
            @Positive
            @PathVariable long id) {
        Customer customer = customerService.findById(id);

        return ResponseEntity.ok(customer);
    }

    @GetMapping("me")
    public ResponseEntity<Customer> getMe() {
        Customer customer = customerService.findMe();

        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody CreatePersonRequest request) {
        Long id = customerService.save(request);

        return ResponseEntity.created(URI.create("/customers/" + id))
                .body(id);
    }

    @PutMapping("{id}")
    public void updateById(
            @Valid
            @Positive
            @PathVariable long id, @Valid @RequestBody UpdatePersonRequest request) {
        customerService.updateById(id, request);
    }

    @PutMapping("me")
    public void updateMe(@RequestBody UpdatePersonRequest request) {
        customerService.updateMe(request);
    }

    @PatchMapping("archive/{id}")
    public void archive(
            @Valid
            @Positive
            @PathVariable long id) {
        customerService.archive(id);
    }

    @DeleteMapping("{id}")
    public void delete(
            @Valid
            @Positive
            @PathVariable long id) {
        customerService.delete(id);
    }
}
