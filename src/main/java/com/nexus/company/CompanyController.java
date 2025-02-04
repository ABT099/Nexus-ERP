package com.nexus.company;

import com.nexus.abstraction.UserContext;
import com.nexus.common.ArchivableQueryType;
import com.nexus.common.ArchivedService;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserDTO;
import com.nexus.user.UserType;
import com.nexus.utils.UpdateHandler;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("companies")
public class CompanyController extends UserContext {

    private final CompanyRepository companyRepository;
    private final UserCreationContext userCreationContext;
    private final CompanyMapper companyMapper;

    public CompanyController(CompanyRepository companyRepository, UserCreationContext userCreationContext, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.userCreationContext = userCreationContext;
        this.companyMapper = companyMapper;
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAll(
            @RequestParam(
                    required = false,
                    name = "a"
            ) ArchivableQueryType queryType) {
        List<Company> companies = ArchivedService.determine(queryType, companyRepository);

        return ResponseEntity.ok(companies.stream().map(companyMapper::map).toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<CompanyResponse> getById(@Valid @Positive @PathVariable long id) {
        Company company = findById(id);

        return ResponseEntity.ok(companyMapper.map(company));
    }

    @GetMapping("me")
    public ResponseEntity<CompanyResponse> getMe() {
        Company company = findFromAuth();

        return ResponseEntity.ok(companyMapper.map(company));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Long> create(@Valid @RequestBody CreateCompanyRequest request) {
        UserDTO userDto = userCreationContext.create(request.username(), request.password(), UserType.CUSTOMER);

        Company company = new Company(userDto.user(), request.companyName());

        companyRepository.save(company);

        return ResponseEntity.created(URI.create("/companies/" + company.getId())).body(company.getId());
    }

    @PutMapping
    public void updateById(@Valid @RequestBody UpdateCompanyRequest request) {
        Company company = findById(request.id());

        UpdateHandler.updateEntity(tracker -> {
            tracker.updateField(company::getCompanyName, request.companyName(), company::setCompanyName);
        }, () -> companyRepository.save(company));
    }

    @PutMapping("me")
    public void updateMe(@RequestBody String companyName) {
        Company company = findFromAuth();

        UpdateHandler.updateEntity(tracker -> {
            tracker.updateField(company::getCompanyName, companyName, company::setCompanyName);
        }, () -> companyRepository.save(company));
    }

    @PatchMapping("archive/{id}")
    @Transactional
    public void archive(@Valid @Positive @PathVariable long id) {
        companyRepository.archiveById(id);
        companyRepository.archiveUserById(id);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable long id) {
        companyRepository.deleteById(id);
    }

    private Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
    }

    private Company findFromAuth() {
        return companyRepository.findByUserId(getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
