package com.nexus.common;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface ArchivableRepository<T, ID> extends JpaRepository<T, ID> {
    @Modifying
    @Transactional
    @Query("""
        UPDATE #{#entityName} e
        SET e.archived = true
        WHERE e.id = :id
    """)
    void archiveById(ID id);

    @Query("""
        SELECT e FROM #{#entityName} e
        WHERE e.archived = false
    """)
    List<T> findAllNonArchived();

    @Query("""
        SELECT e FROM #{#entityName} e
        WHERE e.archived = true
    """)
    List<T> findAllArchived();
}