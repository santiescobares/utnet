package ar.net.ut.backend.auth.exception;

import ar.net.ut.backend.exception.BackendException;
import org.springframework.http.HttpStatus;

public class BannedUserException extends BackendException {

    public BannedUserException(String message) {
        super(message, HttpStatus.FORBIDDEN, "BANNED_USER");
    }
}
