package com.nexus.admin;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserCreationService;
import com.nexus.user.UserType;
import com.nexus.common.abstraction.AbstractUserService;
import com.nexus.common.request.CreatePersonRequest;
import com.nexus.common.request.UpdatePersonRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AdminService extends AbstractUserService {

    private final AdminRepository adminRepository;
    private final UserCreationService userCreationService;

    public AdminService(AdminRepository adminRepository, UserCreationService userCreationService) {
        this.adminRepository = adminRepository;
        this.userCreationService = userCreationService;
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

    public void create(CreatePersonRequest request) {
        User createdUser = userCreationService.create(request.username(), request.password(), UserType.ADMIN);

        Admin admin = new Admin(createdUser, request.firstName(), request.lastName());

        adminRepository.save(admin);
    }

    public void update(UpdatePersonRequest request) {
        Admin admin = adminRepository.findById(getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("admin not found")
                );

        boolean updated = false;

        if (!Objects.equals(admin.getFirstName(), request.firstName())) {
            admin.setFirstName(request.firstName());
            updated = true;
        }

        if (!Objects.equals(admin.getLastName(), request.lastName())) {
            admin.setLastName(request.lastName());
            updated = true;
        }

        if (updated) {
            adminRepository.save(admin);
        }
    }

    public void archive(Long id) {
        adminRepository.archiveById(id);
    }

    public void delete(Long id) {
        adminRepository.deleteById(id);
    }
}
