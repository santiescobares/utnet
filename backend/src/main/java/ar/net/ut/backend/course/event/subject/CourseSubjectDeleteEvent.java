package ar.net.ut.backend.course.event.subject;

import ar.net.ut.backend.course.CourseSubject;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.User;

public class CourseSubjectDeleteEvent extends CourseSubjectEvent {

    public CourseSubjectDeleteEvent(User user, CourseSubject courseSubject) {
        super(user, courseSubject, Log.Action.DELETE);
    }
}
