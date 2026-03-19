package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.CourseEvent;

public class CourseEventResourceCreateEvent extends CourseEventResourceEvent {

    public CourseEventResourceCreateEvent(CourseEvent courseEvent) {
        super(courseEvent);
    }
}
