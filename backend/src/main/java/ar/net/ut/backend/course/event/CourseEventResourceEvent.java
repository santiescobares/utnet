package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.CourseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CourseEventResourceEvent {

    private final CourseEvent courseEvent;
}
