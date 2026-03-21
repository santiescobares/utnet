package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.Course;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.User;

public class CourseUpdateEvent extends CourseEvent {

    public CourseUpdateEvent(User user, Course course) {
        super(user, course, Log.Action.EDIT);
    }
}
