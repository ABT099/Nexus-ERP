package com.nexus.common.person;

import com.nexus.common.ArchivableRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@NoRepositoryBean
public interface PersonRepository<T, ID> extends ArchivableRepository<T, ID> {

    @Query("""
    SELECT e FROM #{#entityName} e
    join e.user u
    where u.id = :userId
    """)
    Optional<T> findByUserId(@Param("userId") Long userId);
}
