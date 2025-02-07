package com.nexus.ineteraction;

import com.nexus.user.UserInfoDTO;
import org.springframework.stereotype.Component;

@Component
public class InteractionMapper {

    public ListInteractionResponse toListInteractionResponse(Interaction interaction) {
        return new ListInteractionResponse(
                interaction.getId(),
                interaction.getTitle(),
                interaction.getInteractionDate().toString()
        );
    }

    public InteractionResponse toInteractionResponse(Interaction interaction) {
        return new InteractionResponse(
                interaction.getId(),
                interaction.getTitle(),
                interaction.getDescription(),
                interaction.getInteractionDate().toString(),
                new UserInfoDTO(
                        interaction.getInteractedBy().getId(),
                        interaction.getInteractedBy().getUsername(),
                        interaction.getInteractedBy().getAvatarUrl()
                )
        );
    }
}
