package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.CourseEvent;

public class CourseEventResourceUpdateEvent extends CourseEventResourceEvent {

    public CourseEventResourceUpdateEvent(CourseEvent courseEvent) {
        super(courseEvent);
    }
}
