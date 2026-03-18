package ar.net.ut.backend.user.event;

import ar.net.ut.backend.user.entity.User;

public class UserCreateEvent extends UserEvent {

    public UserCreateEvent(User user) {
        super(user);
    }
}
