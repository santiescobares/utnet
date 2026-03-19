package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.CourseReview;

public class CourseReviewCreateEvent extends CourseReviewEvent {

    public CourseReviewCreateEvent(CourseReview courseReview) {
        super(courseReview);
    }
}
