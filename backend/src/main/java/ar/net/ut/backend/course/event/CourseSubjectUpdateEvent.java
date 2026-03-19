package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.CourseSubject;

public class CourseSubjectUpdateEvent extends CourseSubjectEvent {

    public CourseSubjectUpdateEvent(CourseSubject courseSubject) {
        super(courseSubject);
    }
}
