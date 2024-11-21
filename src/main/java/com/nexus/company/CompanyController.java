package com.nexus.company;

import com.nexus.common.ArchivableQueryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<List<Company>> getAll(
            @RequestParam(
                    required = false,
                    name = "a"
            ) ArchivableQueryType queryType) {
        List<Company> companies;

        switch (queryType) {
            case ALL -> companies = companyService.findAll();
            case Archived -> companies = companyService.findAllArchived();
            default -> companies = companyService.findAllNonArchived();
        }

        return ResponseEntity.ok(companies);
    }

    @GetMapping("{id}")
    public ResponseEntity<Company> getById(
            @Valid
            @Positive
            @PathVariable long id) {
        Company company = companyService.findById(id);

        return ResponseEntity.ok(company);
    }

    @GetMapping("me")
    public ResponseEntity<Company> getMe() {
        return ResponseEntity.ok(companyService.findMe());
    }

    @PostMapping
    public void create(@Valid @RequestBody CreateCompanyRequest request) {
        companyService.save(request);
    }

    @PutMapping
    public void updateById(
            @Valid
            @RequestBody UpdateCompanyRequest request) {
        companyService.updateById(request);
    }

    @PutMapping("me")
    public void updateMe(@RequestBody String companyName) {
        companyService.updateMe(companyName);
    }

    @PatchMapping("archive/{id}")
    public void archive(
            @Valid
            @Positive
            @PathVariable long id) {
        companyService.archive(id);
    }

    @DeleteMapping("{id}")
    public void delete(
            @Valid
            @Positive
            @PathVariable long id) {
        companyService.delete(id);
    }
}
