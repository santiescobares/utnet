package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.Course;

public class CourseUpdateEvent extends CourseEvent {

    public CourseUpdateEvent(Course course) {
        super(course);
    }
}
