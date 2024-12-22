package com.nexus.repository;

import com.nexus.project.Project;
import com.nexus.project.ProjectRepository;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ProjectRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void shouldFindAllProjectsByOwnerId() {
        User user = new User(
                "username",
                "password",
                UserType.SUPER_USER
        );

        userRepository.save(user);

        Project project = new Project(
                user,
                123,
                "name",
                "description",
                ZonedDateTime.now(),
                ZonedDateTime.now().plusDays(1)
        );

        projectRepository.save(project);

        List<Project> projects = projectRepository.findAllByOwnerId(user.getId());

        assertFalse(projects.isEmpty());
        assertTrue(projects.contains(project));
    }
}
