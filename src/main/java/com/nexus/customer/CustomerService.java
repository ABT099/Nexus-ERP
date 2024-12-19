package com.nexus.customer;

import com.nexus.admin.AdminService;
import com.nexus.abstraction.AbstractUserService;
import com.nexus.person.CreatePersonRequest;
import com.nexus.person.UpdatePersonRequest;
import com.nexus.person.PersonService;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserDto;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService extends AbstractUserService {

    private final CustomerRepository customerRepository;
    private final UserCreationContext userCreationContext;
    private final PersonService<Customer> personService;
    private final AdminService adminService;

    public CustomerService(CustomerRepository customerRepository,
                           UserCreationContext userCreationContext,
                           PersonService<Customer> personService,
                           AdminService adminService) {
        this.customerRepository = customerRepository;
        this.userCreationContext = userCreationContext;
        this.personService = personService;
        this.adminService = adminService;
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
    public Long save(CreatePersonRequest request) {
        adminService.findMe();

        UserDto userDto = userCreationContext.create(request.username(), request.password(), UserType.CUSTOMER);

        Customer customer = new Customer(userDto.user(), request.firstName(), request.lastName());

        customerRepository.save(customer);

        return customer.getId();
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
