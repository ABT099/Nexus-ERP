package com.nexus.project;

import com.nexus.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ProjectFinder {

    private final ProjectRepository projectRepository;

    public ProjectFinder(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project findById(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Project with id " + id + " not found")
                );
    }

    public void doesProjectExist(Integer id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project with id " + id + " not found");
        }
    }
}
