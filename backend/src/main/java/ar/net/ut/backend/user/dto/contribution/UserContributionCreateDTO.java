package ar.net.ut.backend.user.dto.contribution;

import ar.net.ut.backend.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserContributionCreateDTO(
        @NotNull
        ResourceType resourceType,
        @NotBlank
        String resourceId,
        @Positive
        int awardedPoints
) {
}
