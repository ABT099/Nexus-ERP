package com.nexus.person;

import com.nexus.abstraction.AbstractPerson;
import com.nexus.exception.NoUpdateException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PersonService<T extends AbstractPerson> {

    public T updatePerson(T person, UpdatePersonRequest request) {
        boolean updated = false;

        if (!Objects.equals(person.getFirstName(), request.firstName())) {
            person.setFirstName(request.firstName());
            updated = true;
        }

        if (!Objects.equals(person.getLastName(), request.lastName())) {
            person.setLastName(request.lastName());
            updated = true;
        }

        if (!updated) {
            throw new NoUpdateException("no updateMe where made to " + person.getClass().getSimpleName());
        }

        return person;

    }
}
