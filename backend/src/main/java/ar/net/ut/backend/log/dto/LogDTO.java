package ar.net.ut.backend.log.dto;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record LogDTO(
        Long id,
        Instant createdAt,
        UUID userId,
        String userFullName,
        ResourceType resourceType,
        String resourceId,
        Log.Action action,
        Map<String, Object> details
) {
}
