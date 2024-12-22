package com.nexus.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Integer> {
    @Query("""
    select f from File f
    join fetch f.projects p
    where p.id = :projectId
    """)
    List<File> findAllByProjectId(Integer projectId);
}
