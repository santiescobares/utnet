package ar.net.ut.backend.exception.impl;

import ar.net.ut.backend.exception.BackendException;
import org.springframework.http.HttpStatus;

public class ThirdPartyException extends BackendException {

    public ThirdPartyException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "THIRD_PARTY_EXCEPTION");
    }
}
