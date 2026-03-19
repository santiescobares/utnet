package ar.net.ut.backend.auth.event.login;

import ar.net.ut.backend.auth.event.AuthEvent;
import ar.net.ut.backend.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

@Getter
public class PostLoginEvent extends AuthEvent {

    private final User user;

    public PostLoginEvent(HttpServletRequest request, HttpServletResponse response, User user) {
        super(request, response);
        this.user = user;
    }
}
