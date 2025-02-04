package com.nexus.projectstep;

import com.nexus.common.ArchivableRepository;

import java.util.List;

public interface ProjectStepRepository extends ArchivableRepository<ProjectStep, Integer> {
    List<ProjectStep> findAllByProjectId(Integer projectId);
}
