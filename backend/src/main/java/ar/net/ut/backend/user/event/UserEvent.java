package ar.net.ut.backend.user.event;

import ar.net.ut.backend.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class UserEvent {

    private final User user;
}
