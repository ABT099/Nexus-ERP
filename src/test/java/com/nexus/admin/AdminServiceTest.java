package com.nexus.admin;

import com.nexus.AbstractAuthMockTest;
import com.nexus.common.person.CreatePersonRequest;
import com.nexus.common.person.PersonService;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest extends AbstractAuthMockTest {

    @Mock
    private AdminRepository adminRepository;
    @Mock
    private UserCreationContext userCreationContext;
    @Mock
    private PersonService<Admin> personService;
    @InjectMocks
    private AdminService adminService;

    @Test
    void findAll_shouldReturnAllAdmins() {
        // Arrange
        List<Admin> admins = List.of(new Admin());

        when(adminRepository.findAll()).thenReturn(admins);

        // Act
        List<Admin> result = adminService.findAll();

        // Assert
        assertEquals(admins.size(), result.size());
        verify(adminRepository).findAll();
    }

    @Test
    void findAllNonArchived_shouldReturnAllNonArchivedAdmins() {
        // Arrange
        List<Admin> admins = List.of(new Admin());

        when(adminRepository.findAllNonArchived()).thenReturn(admins);

        // Act
        List<Admin> result = adminService.findAllNonArchived();

        // Assert
        assertEquals(admins.size(), result.size());
        verify(adminRepository).findAllNonArchived();
    }

    @Test
    void findAllArchived_shouldReturnAllArchivedAdmins() {
        // Arrange
        List<Admin> admins = List.of(new Admin());

        when(adminRepository.findAllArchived()).thenReturn(admins);

        // Act
        List<Admin> result = adminService.findAllArchived();

        // Assert
        assertEquals(admins.size(), result.size());
        verify(adminRepository).findAllArchived();
    }

    @Test
    void findAllById_shouldReturnAllAdminsByAListOfIds() {
        // Arrange
        List<Admin> admins = List.of(new Admin(), new Admin());
        List<Long> ids = List.of(1L, 2L);

        when(adminRepository.findAllById(ids)).thenReturn(admins);

        // Act
        List<Admin> result = adminService.findAllById(ids);

        // Assert
        assertEquals(admins.size(), result.size());
        verify(adminRepository).findAllById(ids);
    }

    @Test
    void findById_shouldReturnAdmin_whenAdminExists() {
        // Arrange
        Admin admin = new Admin();
        Long id = 1L;

        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));

        // Act
        Admin result = adminService.findById(id);

        // Assert
        assertEquals(admin, result);
        verify(adminRepository).findById(id);
    }

    @Test
    void findById_shouldThrowException_whenAdminDoesNotExists() {
        Long id = 2L;

        when(adminRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.findById(id));

        verify(adminRepository).findById(id);
    }

    @Test
    void findMe_shouldReturnAdmin_whenAuthenticated() {
        // Arrange
        Long id = 1L;

        setUpSecurityContext(id);

        Admin admin = new Admin();
        when(adminRepository.findByUserId(id)).thenReturn(Optional.of(admin));

        // Act
        Admin result = adminService.findMe();

        // Assert
        assertEquals(admin, result);
        verify(adminRepository).findByUserId(id);
    }

    @Test
    void findMe_shouldThrowException_whenIsNotAuthenticated() {
        // Arrange
        Long id = 1L;
        setUpSecurityContext(id);

        when(adminRepository.findByUserId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> adminService.findMe());
        verify(adminRepository).findByUserId(id);
    }


    @Test
    void save() {
        CreatePersonRequest request = new CreatePersonRequest("username", "password", "firstName", "lastName");
        UserDto userDto = new UserDto(new User(), "token");
        when(userCreationContext.create(request.username(), request.password(), UserType.ADMIN)).thenReturn(userDto);

        adminService.save(request);

        verify(userCreationContext).create(request.username(), request.password(), UserType.ADMIN);
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void updateById() {
        Admin admin = new Admin();
        UpdatePersonRequest request = new UpdatePersonRequest("newFirstName", "newLastName");
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(personService.updatePerson(admin, request)).thenReturn(admin);

        adminService.updateById(1L, request);

        verify(adminRepository).findById(1L);
        verify(personService).updatePerson(admin, request);
        verify(adminRepository).save(admin);
    }

    @Test
    void archive() {
        adminService.archive(1L);

        verify(adminRepository).archiveById(1L);
        verify(adminRepository).archiveUserById(1L);
    }

    @Test
    void delete() {
        adminService.delete(1L);

        verify(adminRepository).deleteById(1L);
    }
}