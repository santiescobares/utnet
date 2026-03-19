package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.CourseSubject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CourseSubjectEvent {

    private final CourseSubject courseSubject;
}
