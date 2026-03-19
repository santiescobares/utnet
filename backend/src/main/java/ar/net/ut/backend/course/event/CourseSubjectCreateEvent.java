package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.CourseSubject;

public class CourseSubjectCreateEvent extends CourseSubjectEvent {

    public CourseSubjectCreateEvent(CourseSubject courseSubject) {
        super(courseSubject);
    }
}
