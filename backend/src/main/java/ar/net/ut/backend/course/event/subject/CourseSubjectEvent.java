package ar.net.ut.backend.course.event.subject;

import ar.net.ut.backend.course.CourseSubject;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.user.User;
import lombok.Getter;

@Getter
public abstract class CourseSubjectEvent extends LoggableEvent<CourseSubject> {

    private final CourseSubject courseSubject;

    public CourseSubjectEvent(User user, CourseSubject courseSubject, Log.Action action) {
        super(user, ResourceType.COURSE, courseSubject.getId().toString(), action);
        this.courseSubject = courseSubject;
    }

    @Override
    public CourseSubject getEntity() { return courseSubject; }
}
