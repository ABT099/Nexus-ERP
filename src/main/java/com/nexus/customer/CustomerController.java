package com.nexus.customer;

import com.nexus.abstraction.UserContext;
import com.nexus.common.ArchivableQueryType;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.PersonService;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserDto;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController extends UserContext {

    private final CustomerRepository customerRepository;
    private final UserCreationContext userCreationContext;
    private final PersonService<Customer> personService;

    public CustomerController(CustomerRepository customerRepository, UserCreationContext userCreationContext, PersonService<Customer> personService) {
        this.customerRepository = customerRepository;
        this.userCreationContext = userCreationContext;
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getCustomers(
            @RequestParam(
                    required = false,
                    name = "a"
            )ArchivableQueryType queryType) {
        List<Customer> customers;

        switch (queryType) {
            case ALL -> customers = customerRepository.findAll();
            case Archived -> customers = customerRepository.findAllArchived();
            default -> customers = customerRepository.findAllNonArchived();
        }

        return ResponseEntity.ok(customers);
    }

    @GetMapping("{id}")
    public ResponseEntity<Customer> getById(@Valid @Positive @PathVariable long id) {
        return ResponseEntity.ok(findById(id));
    }

    @GetMapping("me")
    public ResponseEntity<Customer> getMe() {
        return ResponseEntity.ok(findFromAuth());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Long> create(@Valid @RequestBody CreatePersonRequest request) {
        UserDto userDto = userCreationContext.create(request.username(), request.password(), UserType.CUSTOMER);

        Customer customer = new Customer(userDto.user(), request.firstName(), request.lastName());

        customerRepository.save(customer);

        return ResponseEntity
                .created(URI.create("/customers/" + customer.getId()))
                .body(customer.getId());
    }

    @PutMapping("{id}")
    public void updateById(@Valid @Positive @PathVariable long id, @Valid @RequestBody UpdatePersonRequest request) {
        Customer customer = findById(id);

        customer = personService.updatePerson(customer, request);

        customerRepository.save(customer);
    }

    @PutMapping("me")
    public void updateMe(@RequestBody UpdatePersonRequest request) {
        Customer customer = findFromAuth();

        customer = personService.updatePerson(customer, request);

        customerRepository.save(customer);
    }

    @PatchMapping("archive/{id}")
    @Transactional
    public void archive(@Valid @Positive @PathVariable long id) {
        customerRepository.archiveById(id);
        customerRepository.archiveUserById(id);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable long id) {
        customerRepository.deleteById(id);
    }

    private Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("customer with id " + id + " not found")
                );
    }

    private Customer findFromAuth() {
        return customerRepository.findByUserId(getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Error with the authenticated user")
                );
    }
}
