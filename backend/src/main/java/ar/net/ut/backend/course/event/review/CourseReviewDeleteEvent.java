package ar.net.ut.backend.course.event.review;

import ar.net.ut.backend.course.CourseReview;

public class CourseReviewDeleteEvent extends CourseReviewEvent {

    public CourseReviewDeleteEvent(CourseReview courseReview) {
        super(courseReview);
    }
}
