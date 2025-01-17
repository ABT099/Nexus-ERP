package com.nexus.admin;

import com.nexus.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AdminFinder {
    private final AdminRepository adminRepository;

    public AdminFinder(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
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
}
