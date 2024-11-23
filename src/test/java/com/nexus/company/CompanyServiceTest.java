package com.nexus.company;

import com.nexus.AbstractAuthMockTest;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.exception.NoUpdateException;
import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserCreationService;
import com.nexus.user.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest extends AbstractAuthMockTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private UserCreationService userCreationService;
    @InjectMocks
    private CompanyService companyService;

    @Test
    void findAll_shouldReturnAllCompanies() {
        // Arrange
        List<Company> companies = List.of(new Company());

        when(companyRepository.findAll()).thenReturn(companies);

        // Act
        List<Company> result = companyService.findAll();

        // Assert
        assertEquals(companies.size(), result.size());
        verify(companyRepository).findAll();
    }

    @Test
    void findAllNonArchived_shouldReturnAllNonArchivedCompanies() {
        // Arrange
        List<Company> companies = List.of(new Company());

        when(companyRepository.findAllNonArchived()).thenReturn(companies);

        // Act
        List<Company> result = companyService.findAllNonArchived();

        // Assert
        assertEquals(companies.size(), result.size());
        verify(companyRepository).findAllNonArchived();
    }

    @Test
    void findById_shouldReturnCompanyById() {
        // Arrange
        Company company = new Company();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        // Act
        Company result = companyService.findById(1L);

        // Assert
        assertEquals(company, result);
        verify(companyRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenCompanyDoesNotExist() {
        // Arrange
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> companyService.findById(1L)
        );

        verify(companyRepository).findById(1L);
    }

    @Test
    void findMe_shouldReturnCompany_whenAuthenticated() {
        // Arrange
        Long id = 1L;

        setUpSecurityContext(id);

        Company company = new Company();
        when(companyRepository.findByUserId(id)).thenReturn(Optional.of(company));

        // Act
        Company result = companyService.findMe();

        // Assert
        assertEquals(company, result);
        verify(companyRepository).findByUserId(id);
    }

    @Test
    void findMe_shouldThrowException_whenIsNotAuthenticated() {
        // Arrange
        Long id = 1L;
        setUpSecurityContext(id);

        when(companyRepository.findByUserId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> companyService.findMe());
        verify(companyRepository).findByUserId(id);
    }

    @Test
    void save_shouldSaveCompany() {
        CreateCompanyRequest request = new CreateCompanyRequest("companyName", "username", "password");
        User user = new User();
        when(userCreationService.create(request.username(), request.password(), UserType.CUSTOMER)).thenReturn(user);

        companyService.save(request);

        verify(userCreationService).create(request.username(), request.password(), UserType.CUSTOMER);
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void updateById_shouldUpdateWhenTheNameIsNew() {
        Company company = new Company();
        UpdateCompanyRequest request = new UpdateCompanyRequest(1L,"newCompanyName");
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        companyService.updateById(request);

        verify(companyRepository).findById(1L);
        verify(companyRepository).save(company);
    }

    @Test
    void updateById_shouldThrowException_whenCompanyNameIsTheSame() {
        Company company = new Company();
        company.setCompanyName("companyName");
        UpdateCompanyRequest request = new UpdateCompanyRequest(1L,"companyName");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        assertThrows(NoUpdateException.class, () -> companyService.updateById(request));

        verify(companyRepository).findById(1L);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void archive() {
        companyService.archive(1L);

        verify(companyRepository).archiveById(1L);
        verify(companyRepository).archiveUserById(1L);
    }

    @Test
    void delete() {
        companyService.delete(1L);

        verify(companyRepository).deleteById(1L);
    }
}
