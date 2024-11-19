package com.nexus.admin;

import com.nexus.common.ArchivableQueryType;
import com.nexus.common.request.CreatePersonRequest;
import com.nexus.common.request.UpdatePersonRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admins")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<List<Admin>> getAll(
            @RequestParam(
                    required = false,
                    name = "a"
            ) ArchivableQueryType archived
    ) {
        List<Admin> admins;

        switch (archived) {
            case NonArchived -> admins = adminService.getAllNonArchived();
            case Archived -> admins = adminService.getAllArchived();
            default -> admins = adminService.getAll();
        }

        return ResponseEntity.ok(admins);
    }

    @GetMapping("{id}")
    public ResponseEntity<Admin> getById(
            @Valid
            @Positive
            @PathVariable long id) {
        Admin admin = adminService.getById(id);

        return ResponseEntity.ok(admin);
    }

    @GetMapping("me")
    public ResponseEntity<Admin> getMe() {
        Admin admin = adminService.getMe();

        return ResponseEntity.ok(admin);
    }

    @PostMapping
    public void create(@Valid @RequestBody CreatePersonRequest request) {
        adminService.create(request);
    }

    @PutMapping
    public void update(@RequestBody UpdatePersonRequest request) {
        adminService.update(request);
    }

    @PatchMapping("archive/{id}")
    public void archive(
            @Valid
            @Positive
            @PathVariable long id) {
        adminService.archive(id);
    }

    @DeleteMapping("{id}")
    public void delete(
            @Valid
            @Positive
            @PathVariable long id) {
        adminService.delete(id);
    }
}
