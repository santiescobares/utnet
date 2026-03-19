package ar.net.ut.backend.course.event.event;

public class CourseEventResourceCreateEvent extends CourseEventResourceEvent {

    public CourseEventResourceCreateEvent(ar.net.ut.backend.course.CourseEvent courseEvent) {
        super(courseEvent);
    }
}
