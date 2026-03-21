package ar.net.ut.backend.course.event.event;

import ar.net.ut.backend.log.Log;

public class CourseEventResourceUpdateEvent extends CourseEventResourceEvent {

    public CourseEventResourceUpdateEvent(ar.net.ut.backend.course.CourseEvent courseEvent) {
        super(courseEvent, Log.Action.EDIT);
    }
}
