package com.nexus.company;

import com.nexus.common.abstraction.AbstractUserService;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserCreationService;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CompanyService extends AbstractUserService {

    private final CompanyRepository companyRepository;
    private final UserCreationService userCreationService;

    public CompanyService(CompanyRepository companyRepository, UserCreationService userCreationService) {
        this.companyRepository = companyRepository;
        this.userCreationService = userCreationService;
    }

    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    public List<Company> getAllNonArchived() {
        return companyRepository.findAllNonArchived();
    }

    public List<Company> getAllArchived() {
        return companyRepository.findAllArchived();
    }

    public Company getById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
    }

    public Company getMe() {
        return companyRepository.findById(getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public void create(CreateCompanyRequest request) {
        User createdUser = userCreationService.create(request.username(), request.password(), UserType.CUSTOMER);

        Company company = new Company(createdUser, request.companyName());

        companyRepository.save(company);
    }

    public void updateById(UpdateCompanyRequest updateCompanyRequest) {
        Company company = getById(updateCompanyRequest.id());

        if (!Objects.equals(company.getCompanyName(), updateCompanyRequest.companyName())) {
            company.setCompanyName(updateCompanyRequest.companyName());
            companyRepository.save(company);
        }
    }

    public void updateMe(String companyName) {
        Company company = getMe();

        if (!Objects.equals(company.getCompanyName(), companyName)) {
            company.setCompanyName(companyName);
            companyRepository.save(company);
        }
    }

    public void archive(Long id) {
        companyRepository.archiveById(id);
    }

    public void delete(Long id) {
        companyRepository.deleteById(id);
    }
}
