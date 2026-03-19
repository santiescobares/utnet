package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.Course;

public class CourseCreateEvent extends CourseEvent {

    public CourseCreateEvent(Course course) {
        super(course);
    }
}
