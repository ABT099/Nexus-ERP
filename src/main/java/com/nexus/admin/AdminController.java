package com.nexus.admin;

import com.nexus.abstraction.UserContext;
import com.nexus.auth.RegisterResponse;
import com.nexus.common.ArchivableQueryType;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.PersonService;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserDto;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/admins")
public class AdminController extends UserContext {

    private final AdminRepository adminRepository;
    private final UserCreationContext userCreationContext;
    private final PersonService<Admin> personService;
    private final AdminFinder adminFinder;

    public AdminController(AdminRepository adminRepository, UserCreationContext userCreationContext, PersonService<Admin> personService, AdminFinder adminFinder) {
        this.adminRepository = adminRepository;
        this.userCreationContext = userCreationContext;
        this.personService = personService;
        this.adminFinder = adminFinder;
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
            case ALL -> admins = adminRepository.findAll();
            case Archived -> admins = adminRepository.findAllArchived();
            default -> admins = adminRepository.findAllNonArchived();
        }

        return ResponseEntity.ok(admins);
    }

    @GetMapping("{id}")
    public ResponseEntity<Admin> getById(@Valid @Positive @PathVariable long id) {
        return ResponseEntity.ok(adminFinder.findById(id));
    }

    @GetMapping("me")
    public ResponseEntity<Admin> getMe() {
        return ResponseEntity
                .ok(findFromAuth());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<RegisterResponse> create(@Valid @RequestBody CreatePersonRequest request) {
        UserDto userDto = userCreationContext.create(request.username(), request.password(), UserType.ADMIN);

        Admin admin = new Admin(userDto.user(), request.firstName(), request.lastName());

        adminRepository.save(admin);

        return ResponseEntity
                .created(URI.create("/admins/" + admin.getId()))
                .body(new RegisterResponse(admin.getId(), userDto.token()));
    }

    @PutMapping("{id}")
    public void updateById(@Valid @Positive @PathVariable long id, @Valid @RequestBody UpdatePersonRequest request) {
        Admin admin = adminFinder.findById(id);

        admin = personService.updatePerson(admin, request);

        adminRepository.save(admin);
    }

    @PutMapping("me")
    public void updateMe(@RequestBody UpdatePersonRequest request) {
        Admin admin = findFromAuth();

        admin = personService.updatePerson(admin, request);

        adminRepository.save(admin);
    }

    @PatchMapping("archive/{id}")
    @Transactional
    public void archive(@Valid @Positive @PathVariable long id) {
        adminRepository.archiveById(id);
        adminRepository.archiveUserById(id);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable long id) {
        adminRepository.deleteById(id);
    }

    private Admin findFromAuth() {
        return adminRepository.findByUserId(getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Admin with user id " + getUserId() + " not found")
                );
    }
}
