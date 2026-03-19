package ar.net.ut.backend.course.event.subject;

import ar.net.ut.backend.course.CourseSubject;

public class CourseSubjectUpdateEvent extends CourseSubjectEvent {

    public CourseSubjectUpdateEvent(CourseSubject courseSubject) {
        super(courseSubject);
    }
}
