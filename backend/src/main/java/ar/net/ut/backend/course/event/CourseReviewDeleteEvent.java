package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.CourseReview;

public class CourseReviewDeleteEvent extends CourseReviewEvent {

    public CourseReviewDeleteEvent(CourseReview courseReview) {
        super(courseReview);
    }
}
