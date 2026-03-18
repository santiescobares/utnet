package ar.net.ut.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO remove for production
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDTO> handleGenericExceptions(Exception e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "GENERIC_EXCEPTION", e.getMessage());
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ExceptionResponseDTO> handleGenericObjectExceptions(Exception e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "GENERIC_OBJECT_EXCEPTION", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDTO> handleJakartaExceptions(MethodArgumentNotValidException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_EXCEPTION", e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(";"))
        );
    }

    @ExceptionHandler(BackendException.class)
    public ResponseEntity<ExceptionResponseDTO> handleBackendException(BackendException e) {
        return buildResponse(e.getStatusCode(), e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            AuthenticationException.class
    })
    public ResponseEntity<ExceptionResponseDTO> handleUnauthorizedOperation(Exception e) {
        return buildResponse(HttpStatus.FORBIDDEN, "UNAUTHORIZED_OPERATION", e.getMessage());
    }

    public static ResponseEntity<ExceptionResponseDTO> buildResponse(HttpStatus status, String errorCode, String message) {
        if (status.is5xxServerError()) {
            log.error("Internal Server Error ({}): {}", status.value(), message);
        } else {
            log.warn("Client Error ({}): {}", status.value(), message);
        }
        return ResponseEntity.status(status).body(new ExceptionResponseDTO(errorCode, message, Instant.now()));
    }
}
