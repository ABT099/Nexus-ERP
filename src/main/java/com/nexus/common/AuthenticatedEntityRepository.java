package com.nexus.common;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@NoRepositoryBean
public interface AuthenticatedEntityRepository<T, ID> extends ArchivableRepository<T, ID> {
    @Query("""
    SELECT e FROM #{#entityName} e
    join fetch e.user u
    where u.id = :userId
    """)
    Optional<T> findByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.archived = true WHERE u = (SELECT e.user FROM #{#entityName} e WHERE e.id = :id)")
    void archiveUserById(@Param("id") ID id);
}
