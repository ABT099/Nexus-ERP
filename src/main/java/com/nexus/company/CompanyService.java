package com.nexus.company;

import com.nexus.admin.AdminService;
import com.nexus.common.abstraction.AbstractUserService;
import com.nexus.exception.NoUpdateException;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CompanyService extends AbstractUserService {

    private final CompanyRepository companyRepository;
    private final AdminService adminService;
    private final UserCreationContext userCreationContext;

    public CompanyService(CompanyRepository companyRepository, AdminService adminService, UserCreationContext userCreationContext) {
        this.companyRepository = companyRepository;
        this.adminService = adminService;
        this.userCreationContext = userCreationContext;
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public List<Company> findAllNonArchived() {
        return companyRepository.findAllNonArchived();
    }

    public List<Company> findAllArchived() {
        return companyRepository.findAllArchived();
    }

    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
    }

    public Company findMe() {
        return companyRepository.findByUserId(getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public Long save(CreateCompanyRequest request) {
        adminService.findMe();

        UserDto userDto = userCreationContext.create(request.username(), request.password(), UserType.CUSTOMER);

        Company company = new Company(userDto.user(), request.companyName());

        companyRepository.save(company);

        return company.getId();
    }

    @Transactional
    public void updateById(UpdateCompanyRequest updateCompanyRequest) {
        Company company = findById(updateCompanyRequest.id());

        if (!Objects.equals(company.getCompanyName(), updateCompanyRequest.companyName())) {
            company.setCompanyName(updateCompanyRequest.companyName());
            companyRepository.save(company);
        } else {
            throw new NoUpdateException("No update has been made");
        }
    }

    @Transactional
    public void updateMe(String companyName) {
        Company company = findMe();

        if (!Objects.equals(company.getCompanyName(), companyName)) {
            company.setCompanyName(companyName);
            companyRepository.save(company);
        } else {
            throw new NoUpdateException("No update has been made");
        }
    }

    @Transactional
    public void archive(Long id) {
        companyRepository.archiveById(id);
        companyRepository.archiveUserById(id);
    }

    @Transactional
    public void delete(Long id) {
        companyRepository.deleteById(id);
    }
}
