package ar.net.ut.backend.course.event.review;

import ar.net.ut.backend.course.CourseReview;
import ar.net.ut.backend.log.Log;

public class CourseReviewDeleteEvent extends CourseReviewEvent {

    public CourseReviewDeleteEvent(CourseReview courseReview) {
        super(courseReview, Log.Action.DELETE);
    }
}
