package com.nexus.customer;

import com.nexus.common.abstraction.AbstractUserService;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.common.person.PersonService;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserCreationService;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService extends AbstractUserService {

    private final CustomerRepository customerRepository;
    private final UserCreationService userCreationService;
    private final PersonService<Customer> personService;

    public CustomerService(CustomerRepository customerRepository,
                           UserCreationService userCreationService,
                           PersonService<Customer> personService) {
        this.customerRepository = customerRepository;
        this.userCreationService = userCreationService;
        this.personService = personService;
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public List<Customer> getAllNonArchived() {
        return customerRepository.findAllNonArchived();
    }

    public List<Customer> getAllArchived() {
        return customerRepository.findAllArchived();
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("customer with id " + id + " not found")
                );
    }

    public Customer getMe() {
        return getById(getUserId());
    }

    @Transactional
    public void create(CreatePersonRequest request) {
        User createdUser = userCreationService.create(request.username(), request.password(), UserType.ADMIN);

        Customer customer = new Customer(createdUser, request.firstName(), request.lastName());

        customerRepository.save(customer);
    }

    public void updateById(Long id, UpdatePersonRequest request) {
        Customer customer = getById(id);

        customer = personService.updatePerson(customer, request);

        customerRepository.save(customer);
    }

    public void updateMe(UpdatePersonRequest request) {
        Customer customer = getMe();

       customer = personService.updatePerson(customer, request);

       customerRepository.save(customer);
    }

    public void archive(Long id) {
        customerRepository.archiveById(id);
    }

    public void delete(Long id) {
        customerRepository.deleteById(id);
    }
}
