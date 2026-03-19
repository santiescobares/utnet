package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.CourseEvent;

public class CourseEventResourceDeleteEvent extends CourseEventResourceEvent {

    public CourseEventResourceDeleteEvent(CourseEvent courseEvent) {
        super(courseEvent);
    }
}
