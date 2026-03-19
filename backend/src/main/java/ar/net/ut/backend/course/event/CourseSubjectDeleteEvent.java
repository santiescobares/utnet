package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.CourseSubject;

public class CourseSubjectDeleteEvent extends CourseSubjectEvent {

    public CourseSubjectDeleteEvent(CourseSubject courseSubject) {
        super(courseSubject);
    }
}
