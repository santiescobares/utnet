package ar.net.ut.backend.course.event.review;

import ar.net.ut.backend.course.CourseReview;
import ar.net.ut.backend.model.event.CommentAddInteractionEvent;
import ar.net.ut.backend.user.UserInteraction;

public class CourseReviewAddInteractionEvent extends CommentAddInteractionEvent<CourseReview> {

    public CourseReviewAddInteractionEvent(CourseReview review, UserInteraction.Type interactionType) {
        super(review, interactionType);
    }
}
