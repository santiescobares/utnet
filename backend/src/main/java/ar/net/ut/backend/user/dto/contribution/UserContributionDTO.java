package ar.net.ut.backend.user.dto.contribution;

import ar.net.ut.backend.enums.ResourceType;

import java.time.Instant;

public record UserContributionDTO(
        Long id,
        Instant createdAt,
        ResourceType resourceType,
        String resourceId,
        int awardedPoints
) {
}
