package ar.net.ut.backend.exception;

import java.time.Instant;

public record ExceptionResponseDTO(
        String errorCode,
        String message,
        Instant timestamp
) {
}
