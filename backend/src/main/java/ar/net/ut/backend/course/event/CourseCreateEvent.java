package ar.net.ut.backend.course.event;


import ar.net.ut.backend.course.Course;
import ar.net.ut.backend.course.event.event.CourseEvent;

public class CourseCreateEvent extends CourseEvent {

    public CourseCreateEvent(Course course) {
        super(course);
    }
}
