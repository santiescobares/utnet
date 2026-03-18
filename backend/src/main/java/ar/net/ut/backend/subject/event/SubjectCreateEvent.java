package ar.net.ut.backend.subject.event;

import ar.net.ut.backend.subject.Subject;

public class SubjectCreateEvent extends SubjectEvent {

    public SubjectCreateEvent(Subject subject) {
        super(subject);
    }
}
