package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.Course;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.user.User;
import lombok.Getter;

@Getter
public abstract class CourseEvent extends LoggableEvent<Course> {

    private final Course course;

    public CourseEvent(User user, Course course, Log.Action action) {
        super(user, ResourceType.COURSE, course.getId().toString(), action);
        this.course = course;
    }

    @Override
    public Course getEntity() { return course; }
}
