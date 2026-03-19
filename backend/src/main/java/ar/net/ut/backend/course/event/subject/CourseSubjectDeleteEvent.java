package ar.net.ut.backend.course.event.subject;

import ar.net.ut.backend.course.CourseSubject;

public class CourseSubjectDeleteEvent extends CourseSubjectEvent {

    public CourseSubjectDeleteEvent(CourseSubject courseSubject) {
        super(courseSubject);
    }
}
