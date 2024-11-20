package com.nexus.admin;

import com.nexus.common.person.PersonService;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserCreationService;
import com.nexus.user.UserType;
import com.nexus.common.abstraction.AbstractUserService;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.UpdatePersonRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService extends AbstractUserService {

    private final AdminRepository adminRepository;
    private final UserCreationService userCreationService;
    private final PersonService<Admin> personService;

    public AdminService(AdminRepository adminRepository, UserCreationService userCreationService, PersonService<Admin> personService) {
        this.adminRepository = adminRepository;
        this.userCreationService = userCreationService;
        this.personService = personService;
    }

    public List<Admin> getAll() {
        return adminRepository.findAll();
    }

    public List<Admin> getAllNonArchived() {
        return adminRepository.findAllNonArchived();
    }

    public List<Admin> getAllArchived() {
        return adminRepository.findAllArchived();
    }

    public List<Admin> getAllByIds(Iterable<Long> ids) {
        return adminRepository.findAllById(ids);
    }

    public Admin getById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("admin with id " + id + " not found")
                );
    }

    public Admin getMe() {
        return adminRepository.findById(getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("admin not found")
                );
    }

    @Transactional
    public void create(CreatePersonRequest request) {
        User createdUser = userCreationService.create(request.username(), request.password(), UserType.ADMIN);

        Admin admin = new Admin(createdUser, request.firstName(), request.lastName());

        adminRepository.save(admin);
    }

    public void updateById(Long id, UpdatePersonRequest request) {
        Admin admin = getById(id);

        admin = personService.updatePerson(admin, request);

        adminRepository.save(admin);
    }

    public void updateMe(UpdatePersonRequest request) {
        Admin admin = getMe();

        admin = personService.updatePerson(admin, request);

        adminRepository.save(admin);
    }

    public void archive(Long id) {
        adminRepository.archiveById(id);
    }

    public void delete(Long id) {
        adminRepository.deleteById(id);
    }
}
