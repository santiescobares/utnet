package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.Course;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.User;

public class CourseDeleteEvent extends CourseEvent {

    public CourseDeleteEvent(User user, Course course) {
        super(user, course, Log.Action.DELETE);
    }
}
