package com.nexus.projectstep;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProjectStepRepository extends CrudRepository<ProjectStep, Integer> {
    List<ProjectStep> findAllByProjectId(Integer projectId);
}
