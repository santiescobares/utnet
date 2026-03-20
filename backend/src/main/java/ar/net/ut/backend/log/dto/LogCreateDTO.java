package ar.net.ut.backend.log.dto;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record LogCreateDTO(

        @NotNull(message = "El tipo de recurso es obligatorio")
        ResourceType resourceType,

        @NotBlank(message = "El ID del recurso es obligatorio")
        String resourceId,

        @NotNull(message = "La acción es obligatoria")
        Log.Action action,

        Map<String, Object> details
) {
}
