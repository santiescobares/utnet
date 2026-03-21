package ar.net.ut.backend.user.event;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.user.User;
import lombok.Getter;

@Getter
public abstract class UserEvent extends LoggableEvent<User> {

    private final User user;

    public UserEvent(User user, Log.Action action) {
        super(user, ResourceType.USER, user.getId().toString(), action);
        this.user = user;
    }

    @Override
    public User getEntity() { return user; }
}
