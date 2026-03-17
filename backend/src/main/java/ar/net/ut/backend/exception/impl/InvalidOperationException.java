package ar.net.ut.backend.exception.impl;

import ar.net.ut.backend.exception.BackendException;
import org.springframework.http.HttpStatus;

public class InvalidOperationException extends BackendException {

    public InvalidOperationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_OPERATION");
    }
}
