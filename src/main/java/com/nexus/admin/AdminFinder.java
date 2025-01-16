package com.nexus.admin;

import com.nexus.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AdminFinder {
    private final AdminRepository adminRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminFinder.class);

    public AdminFinder(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public List<Admin> findAllById(Set<Long> ids) {
        return adminRepository.findAllById(ids);
    }

    public Admin findById(Long id) {
        LOGGER.info("Finding admin by id: {}", id);
        Optional<Admin> admin =  adminRepository.findById(id);

        if (admin.isPresent()) {
            return admin.get();
        }

        LOGGER.error("Admin with id {} is not found", id);
        throw new ResourceNotFoundException("Admin with id " + id + " not found");
    }
}
