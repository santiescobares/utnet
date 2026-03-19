package ar.net.ut.backend.user.dto.interaction;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.UserInteraction;

import java.time.Instant;
import java.util.UUID;

public record UserInteractionDTO(
        Long id,
        Instant createdAt,
        UUID userId,
        UserInteraction.Type type,
        ResourceType resourceType,
        String resourceId
) {
}
