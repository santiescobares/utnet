package ar.net.ut.backend.auth.token;

import ar.net.ut.backend.exception.BackendException;
import org.springframework.http.HttpStatus;

public class TokenException extends BackendException {

    public TokenException(String message, HttpStatus statusCode) {
        super(message, statusCode, "INVALID_TOKEN");
    }
}
