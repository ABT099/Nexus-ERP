package com.nexus.repository;

import com.nexus.project.Project;
import com.nexus.project.ProjectRepository;
import com.nexus.projectstep.ProjectStep;
import com.nexus.projectstep.ProjectStepRepository;
import com.nexus.tenant.Tenant;
import com.nexus.tenant.TenantRepository;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectStepRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectStepRepository projectStepRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void shouldFindAllProjectsByOwnerId() {
        Tenant tenant = tenantRepository.save(new Tenant());

        User user = new User(
                "username",
                "password",
                UserType.SUPER_USER,
                tenant.getId()
        );

        userRepository.save(user);

        Project project = new Project(
                user,
                123,
                "name",
                "description",
                ZonedDateTime.now(),
                ZonedDateTime.now().plusDays(1),
                tenant.getId()
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
