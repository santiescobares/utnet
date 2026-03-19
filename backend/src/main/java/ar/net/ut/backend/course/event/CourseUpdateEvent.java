package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.Course;
import ar.net.ut.backend.course.event.event.CourseEvent;

public class CourseUpdateEvent extends CourseEvent {

    public CourseUpdateEvent(Course course) {
        super(course);
    }
}
