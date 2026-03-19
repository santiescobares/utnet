package ar.net.ut.backend.course.event.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CourseEventResourceEvent {

    private final ar.net.ut.backend.course.CourseEvent courseEvent;
}
