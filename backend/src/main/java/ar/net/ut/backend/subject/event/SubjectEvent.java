package ar.net.ut.backend.subject.event;

import ar.net.ut.backend.subject.Subject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class SubjectEvent {

    private final Subject subject;
}
