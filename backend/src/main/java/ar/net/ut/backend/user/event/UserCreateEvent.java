package ar.net.ut.backend.user.event;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.User;

public class UserCreateEvent extends UserEvent {

    public UserCreateEvent(User user) {
        super(user, Log.Action.CREATE);
    }
}
