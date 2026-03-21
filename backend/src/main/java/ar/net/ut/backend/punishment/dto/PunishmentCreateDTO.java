package ar.net.ut.backend.punishment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record PunishmentCreateDTO(
        @NotNull(message = "User ID is required")
        UUID userId,

        @NotBlank(message = "Reason is required")
        @Size(min = 10, max = 500, message = "Reason is either too short or too long")
        String reason,

        @Future(message = "Expiration date must be in the future")
        LocalDateTime expirationDate
) {
}
