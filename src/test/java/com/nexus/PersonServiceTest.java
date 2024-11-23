package com.nexus;

import com.nexus.common.abstraction.AbstractPerson;
import com.nexus.common.person.PersonService;
import com.nexus.common.person.UpdatePersonRequest;
import com.nexus.exception.NoUpdateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonServiceTest {
    private PersonService<AbstractPerson> personService;

    @BeforeEach
    void setUp() {
        personService = new PersonService<>();
    }

    @Test
    void updatePerson_shouldUpdateFirstNameAndLastName_whenDifferentFromRequest() {
        // Arrange
        AbstractPerson person = new MockPerson("OldFirstName", "OldLastName");
        UpdatePersonRequest request = new UpdatePersonRequest("NewFirstName", "NewLastName");

        // Act
        AbstractPerson updatedPerson = personService.updatePerson(person, request);

        // Assert
        assertEquals("NewFirstName", updatedPerson.getFirstName());
        assertEquals("NewLastName", updatedPerson.getLastName());
    }

    @Test
    void updatePerson_shouldUpdateOnlyFirstName_whenLastNameIsSame() {
        // Arrange
        AbstractPerson person = new MockPerson("OldFirstName", "SameLastName");
        UpdatePersonRequest request = new UpdatePersonRequest("NewFirstName", "SameLastName");

        // Act
        AbstractPerson updatedPerson = personService.updatePerson(person, request);

        // Assert
        assertEquals("NewFirstName", updatedPerson.getFirstName());
        assertEquals("SameLastName", updatedPerson.getLastName());
    }

    @Test
    void updatePerson_shouldUpdateOnlyLastName_whenFirstNameIsSame() {
        // Arrange
        AbstractPerson person = new MockPerson("SameFirstName", "OldLastName");
        UpdatePersonRequest request = new UpdatePersonRequest("SameFirstName", "NewLastName");

        // Act
        AbstractPerson updatedPerson = personService.updatePerson(person, request);

        // Assert
        assertEquals("SameFirstName", updatedPerson.getFirstName());
        assertEquals("NewLastName", updatedPerson.getLastName());
    }

    @Test
    void updatePerson_shouldThrowNoUpdateException_whenNoChangesAreMade() {
        // Arrange
        AbstractPerson person = new MockPerson("SameFirstName", "SameLastName");
        UpdatePersonRequest request = new UpdatePersonRequest("SameFirstName", "SameLastName");

        // Act & Assert
        assertThrows(
                NoUpdateException.class,
                () -> personService.updatePerson(person, request)
        );
    }

}

class MockPerson extends AbstractPerson {

    private String firstName;
    private String lastName;

    public MockPerson(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}