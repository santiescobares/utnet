package ar.net.ut.backend.course.event.review;

import ar.net.ut.backend.course.CourseReview;
import ar.net.ut.backend.log.Log;

public class CourseReviewCreateEvent extends CourseReviewEvent {

    public CourseReviewCreateEvent(CourseReview courseReview) {
        super(courseReview, Log.Action.CREATE);
    }
}
