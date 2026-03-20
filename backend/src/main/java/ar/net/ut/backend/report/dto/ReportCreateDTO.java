package ar.net.ut.backend.report.dto;

import ar.net.ut.backend.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportCreateDTO(

        @NotNull(message = "El tipo de recurso es obligatorio")
        ResourceType resourceType,

        @NotBlank(message = "El ID del recurso es obligatorio")
        String resourceId,

        @NotBlank(message = "El motivo del reporte es obligatorio")
        @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
        String reason
) {
}
