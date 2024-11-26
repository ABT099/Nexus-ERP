package com.nexus.admin;

import com.nexus.auth.RegisterResponse;
import com.nexus.common.ArchivableQueryType;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.UpdatePersonRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

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
            case ALL -> admins = adminService.findAll();
            case Archived -> admins = adminService.findAllArchived();
            default -> admins = adminService.findAllNonArchived();
        }

        return ResponseEntity.ok(admins);
    }

    @GetMapping("{id}")
    public ResponseEntity<Admin> getById(
            @Valid
            @Positive
            @PathVariable long id) {
        Admin admin = adminService.findById(id);

        return ResponseEntity.ok(admin);
    }

    @GetMapping("me")
    public ResponseEntity<Admin> getMe() {
        Admin admin = adminService.findMe();

        return ResponseEntity.ok(admin);
    }

    @PostMapping
    public ResponseEntity<RegisterResponse> create(@Valid @RequestBody CreatePersonRequest request) {
        Pair<Long, String> result = adminService.save(request);

        return ResponseEntity
                .created(URI.create("/admins/" + result.a))
                .body(new RegisterResponse(result.a, result.b));
    }

    @PutMapping("{id}")
    public void updateById(
            @Valid
            @Positive
            @PathVariable long id, @Valid @RequestBody UpdatePersonRequest request) {
        adminService.updateById(id, request);
    }

    @PutMapping("me")
    public void updateMe(@RequestBody UpdatePersonRequest request) {
        adminService.updateMe(request);
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
