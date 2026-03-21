package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.Course;

public class CourseDeleteEvent extends CourseEvent {

    public CourseDeleteEvent(Course course) {
        super(course);
    }
}
