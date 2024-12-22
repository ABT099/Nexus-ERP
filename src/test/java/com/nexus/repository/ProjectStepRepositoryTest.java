package com.nexus.repository;

import com.nexus.project.Project;
import com.nexus.project.ProjectRepository;
import com.nexus.projectstep.ProjectStep;
import com.nexus.projectstep.ProjectStepRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ProjectStepRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectStepRepository projectStepRepository;

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

        List<ProjectStep> steps = List.of(
                new ProjectStep(
                    project, "name1", "description1", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1)
                ),
                new ProjectStep(
                    project, "name2", "description2", ZonedDateTime.now(), ZonedDateTime.now().plusDays(1)
                ));


        projectStepRepository.saveAll(steps);

        List<ProjectStep> actual = projectStepRepository.findAllByProjectId(project.getId());

        assertFalse(actual.isEmpty());
        assertEquals(actual.size(), steps.size());
    }
}
