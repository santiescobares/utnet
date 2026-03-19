package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.Course;

public class CourseDeleteEvent extends CourseEvent {

    public CourseDeleteEvent(Course course) {
        super(course);
    }
}
