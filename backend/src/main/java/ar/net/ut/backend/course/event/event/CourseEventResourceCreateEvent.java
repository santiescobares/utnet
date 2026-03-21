package ar.net.ut.backend.course.event.event;

import ar.net.ut.backend.log.Log;

public class CourseEventResourceCreateEvent extends CourseEventResourceEvent {

    public CourseEventResourceCreateEvent(ar.net.ut.backend.course.CourseEvent courseEvent) {
        super(courseEvent, Log.Action.CREATE);
    }
}
