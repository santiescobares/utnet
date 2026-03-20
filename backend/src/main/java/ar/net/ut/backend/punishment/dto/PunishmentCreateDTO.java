package ar.net.ut.backend.punishment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record PunishmentCreateDTO(

        @NotNull(message = "El ID del usuario es obligatorio")
        UUID userId,

        @NotBlank(message = "El motivo de la sanción es obligatorio")
        @Size(min = 10, max = 500, message = "El motivo debe tener entre 10 y 500 caracteres")
        String reason,

        @NotNull(message = "La fecha de vencimiento es obligatoria")
        @Future(message = "La fecha de vencimiento debe ser en el futuro")
        LocalDateTime expirationDate
) {
}
