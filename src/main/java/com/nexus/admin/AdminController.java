package com.nexus.admin;

import com.nexus.common.ArchivableQueryType;
import com.nexus.common.request.CreatePersonRequest;
import com.nexus.common.request.UpdatePersonRequest;
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
            case null -> admins = adminService.getAll();
            default -> {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok(admins);
    }

    @GetMapping("{id}")
    public ResponseEntity<Admin> getById(@PathVariable long id) {
        Admin admin = adminService.getById(id);

        if (admin == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(admin);
    }

    @GetMapping("me")
    public ResponseEntity<Admin> getMe() {
        Admin admin = adminService.getMe();

        if (admin == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(admin);
    }

    @PostMapping
    public void create(@RequestBody CreatePersonRequest request) {
        adminService.create(request);
    }

    @PutMapping
    public void update(@RequestBody UpdatePersonRequest request) {
        adminService.update(request);
    }

    @PostMapping("archive/{id}")
    public void archive(@PathVariable long id) {
        adminService.archive(id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        adminService.delete(id);
    }
}
