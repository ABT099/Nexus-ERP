package com.nexus.company;

import com.nexus.common.repository.ArchivableRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends ArchivableRepository<Company, Long> {

    @Query("""
    select c from Company c
    join c.user u
    where u.id = :userId
    """)
    Optional<Company> findByUserId(@Param("userId") Long userId);
}
