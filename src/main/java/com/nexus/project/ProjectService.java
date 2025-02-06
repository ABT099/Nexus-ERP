package com.nexus.project;

import com.nexus.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Project findById(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Project with id " + id + " not found")
                );
    }

    public void doesProjectExist(Integer id) {
        if (!projectRepository.existsByIdAndArchivedFalse(id)) {
            throw new ResourceNotFoundException("Project with id " + id + " not found");
        }
    }
}
