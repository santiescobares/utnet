package ar.net.ut.backend.user.dto.activity;

import ar.net.ut.backend.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UserActivityDTO(
        @NotNull(message = "Resource type is required")
        ResourceType resourceType,

        @NotBlank(message = "Resource ID is required")
        String resourceId,

        @NotNull(message = "Timestamp is required")
        Instant timestamp
) {
}
