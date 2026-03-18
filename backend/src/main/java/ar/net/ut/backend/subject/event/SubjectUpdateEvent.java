package ar.net.ut.backend.subject.event;

import ar.net.ut.backend.subject.Subject;

public class SubjectUpdateEvent extends SubjectEvent {

    public SubjectUpdateEvent(Subject subject) {
        super(subject);
    }
}
