package ar.net.ut.backend.course.event.subject;

import ar.net.ut.backend.course.CourseSubject;

public class CourseSubjectCreateEvent extends CourseSubjectEvent {

    public CourseSubjectCreateEvent(CourseSubject courseSubject) {
        super(courseSubject);
    }
}
