package com.nexus.project;

import com.nexus.common.ArchivableRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends ArchivableRepository<Project, Integer> {
    @Query("""
    select p from Project p
    join fetch p.owner o
    where o.id = :id and p.archived = false
    """)
    List<Project> findAllByOwnerId(Long id);

    @Query("""
    SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END 
    FROM Project p 
    WHERE p.id = :id AND p.archived = false
    """)
    boolean existsByIdAndArchivedFalse(Integer id);
}
