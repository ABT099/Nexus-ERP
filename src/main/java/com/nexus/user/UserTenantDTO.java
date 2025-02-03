package com.nexus.user;

import java.util.UUID;

public record UserTenantDTO(
        String id,
        UUID tenantId
) { }
