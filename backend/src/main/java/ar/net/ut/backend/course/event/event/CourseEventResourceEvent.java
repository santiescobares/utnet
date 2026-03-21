package ar.net.ut.backend.course.event.event;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import lombok.Getter;

@Getter
public abstract class CourseEventResourceEvent extends LoggableEvent<ar.net.ut.backend.course.CourseEvent> {

    private final ar.net.ut.backend.course.CourseEvent courseEvent;

    public CourseEventResourceEvent(ar.net.ut.backend.course.CourseEvent courseEvent, Log.Action action) {
        super(courseEvent.getCreatedBy(), ResourceType.COURSE_EVENT, courseEvent.getId().toString(), action);
        this.courseEvent = courseEvent;
    }

    @Override
    public ar.net.ut.backend.course.CourseEvent getEntity() { return courseEvent; }
}
