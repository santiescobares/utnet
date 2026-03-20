package ar.net.ut.backend.exception.impl;

import ar.net.ut.backend.exception.BackendException;
import org.springframework.http.HttpStatus;

public class InternalException extends BackendException {

    public InternalException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_EXCEPTION");
    }
}
