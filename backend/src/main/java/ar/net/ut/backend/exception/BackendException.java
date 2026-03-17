package ar.net.ut.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BackendException extends RuntimeException {

    private final HttpStatus statusCode;
    private final String errorCode;

    public BackendException(String message, HttpStatus statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
}
