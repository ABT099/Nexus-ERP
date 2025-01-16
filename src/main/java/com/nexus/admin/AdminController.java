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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admins")
public class AdminController extends UserContext {

    private final AdminRepository adminRepository;
    private final UserCreationContext userCreationContext;
    private final PersonService<Admin> personService;
    private final AdminFinder adminFinder;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);


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
            case ALL -> {
                admins = adminRepository.findAll();
                LOGGER.info("Retrieving all admins from database");
            }
            case Archived -> {
                admins = adminRepository.findAllArchived();
                LOGGER.info("Retrieving all archived admins from database");
            }
            default ->{
                admins = adminRepository.findAllNonArchived();
                LOGGER.info("Retrieving all not archived admins from database");
            }
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
        try {
            adminRepository.archiveById(id);
            adminRepository.archiveUserById(id);
        } catch (Exception e) {
            LOGGER.error("Error occurred when archiving admin with id: {}, error: {}", id, e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable long id) {
        try {
            adminRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error("Error occurred when deleting admin with id: {}, error: {}", id, e.getMessage());
        }
    }

    private Admin findFromAuth() {
        LOGGER.info("Retrieving admin from authentication");
        Optional<Admin> admin = adminRepository.findByUserId(getUserId());

        if (admin.isPresent()) {
            return admin.get();
        }

        LOGGER.error("Unable to find admin from authentication with id {}", getUserId());
        throw new ResourceNotFoundException("User with id " + getUserId() + " not found");
    }
}
