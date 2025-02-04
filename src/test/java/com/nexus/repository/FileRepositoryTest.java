package com.nexus.repository;

import com.nexus.file.File;
import com.nexus.file.FileRepository;
import com.nexus.project.Project;
import com.nexus.project.ProjectRepository;
import com.nexus.tenant.Tenant;
import com.nexus.tenant.TenantRepository;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindAllFilesByProject() {
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

        File file = new File("name", "description", "type", "url", tenant.getId());

        project.addFile(file);

        fileRepository.save(file);

        List<File> files = fileRepository.findAllByProjectId(project.getId());

        assertFalse(files.isEmpty());
        assertTrue(files.contains(file));
    }
}
