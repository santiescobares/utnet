package ar.net.ut.backend.course.event.subject;

import ar.net.ut.backend.course.CourseSubject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CourseSubjectEvent {

    private final CourseSubject courseSubject;
}
