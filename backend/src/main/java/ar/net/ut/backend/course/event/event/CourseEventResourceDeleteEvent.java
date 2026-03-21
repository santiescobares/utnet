package ar.net.ut.backend.course.event.event;

import ar.net.ut.backend.log.Log;

public class CourseEventResourceDeleteEvent extends CourseEventResourceEvent {

    public CourseEventResourceDeleteEvent(ar.net.ut.backend.course.CourseEvent courseEvent) {
        super(courseEvent, Log.Action.DELETE);
    }
}
