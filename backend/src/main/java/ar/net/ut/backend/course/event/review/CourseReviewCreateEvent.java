package ar.net.ut.backend.course.event.review;

import ar.net.ut.backend.course.CourseReview;

public class CourseReviewCreateEvent extends CourseReviewEvent {

    public CourseReviewCreateEvent(CourseReview courseReview) {
        super(courseReview);
    }
}
