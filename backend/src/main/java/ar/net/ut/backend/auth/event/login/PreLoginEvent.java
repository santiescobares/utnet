package ar.net.ut.backend.auth.event.login;

import ar.net.ut.backend.auth.event.AuthEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PreLoginEvent extends AuthEvent {

    public PreLoginEvent(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }
}
