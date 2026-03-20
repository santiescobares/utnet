package ar.net.ut.backend.punishment.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record PunishmentDTO(
        Long id,
        Instant createdAt,
        UUID userId,
        String userFullName,
        String reason,
        LocalDateTime expirationDate,
        UUID punishedById,
        boolean active
) {
}
