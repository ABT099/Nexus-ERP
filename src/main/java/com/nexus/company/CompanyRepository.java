package com.nexus.company;

import com.nexus.common.ArchivableRepository;
import com.nexus.common.AuthenticatedEntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends AuthenticatedEntityRepository<Company, Long> {
}
