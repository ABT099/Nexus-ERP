package com.nexus.repository;

import com.nexus.project.Project;
import com.nexus.project.ProjectRepository;
import com.nexus.tenant.Tenant;
import com.nexus.tenant.TenantRepository;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Test
    void shouldFindAllProjectsByOwnerId() {
        Tenant tenant = new Tenant();
        tenantRepository.save(tenant);

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
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                tenant.getId()
        );

        projectRepository.save(project);

        List<Project> projects = projectRepository.findAllByOwnerId(user.getId());

        assertFalse(projects.isEmpty());
        assertTrue(projects.contains(project));
    }

    @Test
    void shouldCheckIfProjectExistsByIdAndArchivedFalse() {
        Tenant tenant = new Tenant();
        tenantRepository.save(tenant);

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
                Instant.now(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                tenant.getId()
        );

        projectRepository.save(project);

        boolean exists = projectRepository.existsByIdAndArchivedFalse(project.getId());

        assertTrue(exists);
    }
}
