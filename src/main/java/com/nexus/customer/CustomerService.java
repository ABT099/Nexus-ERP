package com.nexus.customer;

import com.nexus.common.abstraction.AbstractUserService;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.common.person.PersonService;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserCreationService;
import com.nexus.user.UserDto;
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

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public List<Customer> findAllNonArchived() {
        return customerRepository.findAllNonArchived();
    }

    public List<Customer> findAllArchived() {
        return customerRepository.findAllArchived();
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("customer with id " + id + " not found")
                );
    }

    public Customer findMe() {
        return customerRepository.findByUserId(getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Error with the authenticated user")
                );
    }

    @Transactional
    public void save(CreatePersonRequest request) {
        UserDto userDto = userCreationService.create(request.username(), request.password(), UserType.CUSTOMER);

        Customer customer = new Customer(userDto.user(), request.firstName(), request.lastName());

        customerRepository.save(customer);
    }

    @Transactional
    public void updateById(Long id, UpdatePersonRequest request) {
        Customer customer = findById(id);

        customer = personService.updatePerson(customer, request);

        customerRepository.save(customer);
    }

    @Transactional
    public void updateMe(UpdatePersonRequest request) {
        Customer customer = findMe();

       customer = personService.updatePerson(customer, request);

       customerRepository.save(customer);
    }

    @Transactional
    public void archive(Long id) {
        customerRepository.archiveById(id);
        customerRepository.archiveUserById(id);
    }

    @Transactional
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }
}
