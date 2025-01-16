package com.nexus.common.person;

import com.nexus.abstraction.AbstractPerson;
import com.nexus.exception.NoUpdateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class PersonService<T extends AbstractPerson> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonService.class);

    public T updatePerson(T person, UpdatePersonRequest request) {
        boolean updated = false;

        if (!Objects.equals(person.getFirstName(), request.firstName())) {
            person.setFirstName(request.firstName());
            updated = true;
            LOGGER.debug("old first name: {} changed to {}", person.getFirstName(), request.firstName());
        }

        if (!Objects.equals(person.getLastName(), request.lastName())) {
            person.setLastName(request.lastName());
            updated = true;
            LOGGER.debug("old last name: {} changed to {}", person.getLastName(), request.lastName());
        }

        if (!updated) {
            LOGGER.debug("nothing has been updated");
            throw new NoUpdateException("no updateMe where made to " + person.getClass().getSimpleName());
        }

        return person;

    }
}
