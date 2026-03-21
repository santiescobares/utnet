package ar.net.ut.backend.subject.event;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.subject.Subject;
import ar.net.ut.backend.user.User;

public class SubjectUpdateEvent extends SubjectEvent {

    public SubjectUpdateEvent(User user, Subject subject) {
        super(user, subject, Log.Action.EDIT);
    }
}
