package ar.net.ut.backend.career.event;

import ar.net.ut.backend.career.Career;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.user.User;
import lombok.Getter;

@Getter
public abstract class CareerEvent extends LoggableEvent<Career> {

    private final Career career;

    public CareerEvent(User user, Career career, Log.Action action) {
        super(user, ResourceType.CAREER, career.getId().toString(), action);
        this.career = career;
    }

    @Override
    public Career getEntity() { return career; }
}
