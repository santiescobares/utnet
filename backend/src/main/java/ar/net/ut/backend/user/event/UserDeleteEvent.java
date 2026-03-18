package ar.net.ut.backend.user.event;

import ar.net.ut.backend.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

@Getter
public class UserDeleteEvent extends UserEvent {

    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public UserDeleteEvent(User user, HttpServletRequest request, HttpServletResponse response) {
        super(user);
        this.request = request;
        this.response = response;
    }
}
