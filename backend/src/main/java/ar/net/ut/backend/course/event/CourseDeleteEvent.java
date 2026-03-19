package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.Course;
import ar.net.ut.backend.course.event.event.CourseEvent;

public class CourseDeleteEvent extends CourseEvent {

    public CourseDeleteEvent(Course course) {
        super(course);
    }
}
