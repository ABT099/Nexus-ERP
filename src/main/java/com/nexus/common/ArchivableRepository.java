package com.nexus.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;

@NoRepositoryBean
public interface ArchivableRepository<T, ID> extends JpaRepository<T, ID> {
    @Modifying
    @Query("""
        UPDATE #{#entityName} e
        SET e.archived = true
        WHERE e.id = :id
    """)
    void archiveById(ID id);

    @Modifying
    @Query("UPDATE User u SET u.archived = true WHERE u = (SELECT e.user FROM #{#entityName} e WHERE e.id = :id)")
    void archiveUserById(@Param("id") ID id);

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