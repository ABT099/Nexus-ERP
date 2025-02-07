package com.nexus.ineteraction;

import com.nexus.user.UserInfoDTO;

public record InteractionResponse(
        Long id,
        String title,
        String description,
        String interactionDate,
        UserInfoDTO interactedBy
) { }
