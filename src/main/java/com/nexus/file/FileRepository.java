package com.nexus.file;

import com.nexus.common.ArchivableRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends ArchivableRepository<File, Integer> {
    @Query("""
    select f from File f
    join fetch f.projects p
    where p.id = :projectId and p.archived = false and f.archived = false
    """)
    List<File> findAllByProjectId(Integer projectId);
}
