package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CourseEvent {

    private final Course course;
}
