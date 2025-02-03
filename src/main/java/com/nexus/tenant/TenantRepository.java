package com.nexus.tenant;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TenantRepository extends CrudRepository<Tenant, UUID> {
}
