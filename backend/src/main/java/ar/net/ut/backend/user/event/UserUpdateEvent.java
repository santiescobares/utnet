package ar.net.ut.backend.user.event;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.User;

public class UserUpdateEvent extends UserEvent {

    public UserUpdateEvent(User user) {
        super(user, Log.Action.EDIT);
    }
}
