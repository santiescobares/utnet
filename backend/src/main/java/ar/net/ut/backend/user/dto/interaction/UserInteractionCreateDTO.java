package ar.net.ut.backend.user.dto.interaction;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.entity.UserInteraction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserInteractionCreateDTO(
        @NotNull
        UserInteraction.Type type,
        @NotNull
        ResourceType resourceType,
        @NotBlank
        String resourceId
) {
}
