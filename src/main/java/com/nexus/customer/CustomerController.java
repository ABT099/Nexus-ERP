package com.nexus.customer;

import com.nexus.common.ArchivableQueryType;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.UpdatePersonRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            case ALL -> customers = customerService.getAll();
            case Archived -> customers = customerService.getAllArchived();
            default -> customers = customerService.getAllNonArchived();
        }

        return ResponseEntity.ok(customers);
    }

    @GetMapping("{id}")
    public ResponseEntity<Customer> getById(
            @Valid
            @Positive
            @PathVariable long id) {
        Customer customer = customerService.getById(id);

        return ResponseEntity.ok(customer);
    }

    @GetMapping("me")
    public ResponseEntity<Customer> getMe() {
        Customer customer = customerService.getMe();

        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public void create(@Valid @RequestBody CreatePersonRequest request) {
        customerService.create(request);
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
