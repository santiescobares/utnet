package ar.net.ut.backend.subject.event;

import ar.net.ut.backend.subject.Subject;

public class SubjectDeleteEvent extends SubjectEvent {

    public SubjectDeleteEvent(Subject subject) {
        super(subject);
    }
}
