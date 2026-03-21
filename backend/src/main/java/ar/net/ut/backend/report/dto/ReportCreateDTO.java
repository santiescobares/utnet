package ar.net.ut.backend.report.dto;

import ar.net.ut.backend.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportCreateDTO(
        @NotNull(message = "Resource type is required")
        ResourceType resourceType,

        @NotBlank(message = "Resource ID is required")
        String resourceId,

        @NotBlank(message = "Reason is required")
        @Size(min = 10, max = 500, message = "Reason is either too short or too long")
        String reason
) {
}
