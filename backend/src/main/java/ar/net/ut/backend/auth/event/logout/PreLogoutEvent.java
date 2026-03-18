package ar.net.ut.backend.auth.event.logout;

import ar.net.ut.backend.auth.event.AuthEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PreLogoutEvent extends AuthEvent {

    public PreLogoutEvent(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }
}
