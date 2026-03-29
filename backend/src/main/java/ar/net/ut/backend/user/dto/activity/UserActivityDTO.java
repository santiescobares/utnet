package ar.net.ut.backend.user.dto.activity;

import ar.net.ut.backend.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserActivityDTO(
        @NotNull(message = "Resource type is required")
        ResourceType resourceType,

        @NotBlank(message = "Resource ID is required")
        String resourceId
) {
}
