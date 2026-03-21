package ar.net.ut.backend.subject.event;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.subject.Subject;
import ar.net.ut.backend.user.User;
import lombok.Getter;

@Getter
public abstract class SubjectEvent extends LoggableEvent<Subject> {

    private final Subject subject;

    public SubjectEvent(User user, Subject subject, Log.Action action) {
        super(user, ResourceType.SUBJECT, subject.getId().toString(), action);
        this.subject = subject;
    }

    @Override
    public Subject getEntity() { return subject; }
}
