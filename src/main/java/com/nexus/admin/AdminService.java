package com.nexus.admin;

import com.nexus.common.person.PersonService;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserCreationService;
import com.nexus.user.UserDto;
import com.nexus.user.UserType;
import com.nexus.common.abstraction.AbstractUserService;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.UpdatePersonRequest;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.misc.Pair;
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

    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    public List<Admin> findAllNonArchived() {
        return adminRepository.findAllNonArchived();
    }

    public List<Admin> findAllArchived() {
        return adminRepository.findAllArchived();
    }

    public List<Admin> findAllById(Iterable<Long> ids) {
        return adminRepository.findAllById(ids);
    }

    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("admin with id " + id + " not found")
                );
    }

    public Admin findMe() {
        return adminRepository.findByUserId(getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("error with the authenticated user")
                );
    }

    @Transactional
    public Pair<Long, String> save(CreatePersonRequest request) {
        UserDto userDto = userCreationService.create(request.username(), request.password(), UserType.ADMIN);

        Admin admin = new Admin(userDto.user(), request.firstName(), request.lastName());

        adminRepository.save(admin);

        return new Pair<>(admin.getId(), userDto.token());
    }

    @Transactional
    public void updateById(Long id, UpdatePersonRequest request) {
        Admin admin = findById(id);

        admin = personService.updatePerson(admin, request);

        adminRepository.save(admin);
    }

    @Transactional
    public void updateMe(UpdatePersonRequest request) {
        Admin admin = findMe();

        admin = personService.updatePerson(admin, request);

        adminRepository.save(admin);
    }

    @Transactional
    public void archive(Long id) {
        adminRepository.archiveById(id);
        adminRepository.archiveUserById(id);
    }

    @Transactional
    public void delete(Long id) {
        adminRepository.deleteById(id);
    }
}
