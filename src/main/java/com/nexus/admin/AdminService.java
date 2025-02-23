package com.nexus.admin;

import com.nexus.common.person.CreatePersonRequest;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.CreateUserDTO;
import com.nexus.user.User;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserDTO;
import com.nexus.user.UserType;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final UserCreationContext userCreationContext;


    public AdminService(AdminRepository adminRepository, UserCreationContext userCreationContext) {
        this.adminRepository = adminRepository;
        this.userCreationContext = userCreationContext;
    }

    public List<Admin> findAllById(Set<Long> ids) {
        return adminRepository.findAllById(ids);
    }

    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Admin with id " + id + " not found")
                );
    }

    @Transactional
    public void batchSave(List<CreatePersonRequest> requests) {
        List<User> users = userCreationContext.batchCreate(requests.stream()
            .map(request -> new CreateUserDTO(request.username(), request.password(), UserType.ADMIN))
            .toList()
        );

        adminRepository.saveAll(
            requests.stream()
                .map(request -> new Admin(users.get(requests.indexOf(request)), request.firstName(), request.lastName()))
                .toList()
        );
    }
}
