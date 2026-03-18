package ar.net.ut.backend.auth.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AuthEvent {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
}
