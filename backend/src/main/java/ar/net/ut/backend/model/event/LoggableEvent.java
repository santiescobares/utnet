package ar.net.ut.backend.model.event;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class LoggableEvent<T> {

    private final User user;
    private final ResourceType resourceType;
    private final String resourceId;
    private final Log.Action action;

    public abstract T getEntity();
}
