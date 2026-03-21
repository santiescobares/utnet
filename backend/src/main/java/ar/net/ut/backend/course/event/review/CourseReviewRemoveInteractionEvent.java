package ar.net.ut.backend.course.event.review;

import ar.net.ut.backend.course.CourseReview;
import ar.net.ut.backend.model.event.CommentRemoveInteractionEvent;
import ar.net.ut.backend.user.UserInteraction;

public class CourseReviewRemoveInteractionEvent extends CommentRemoveInteractionEvent<CourseReview> {

    public CourseReviewRemoveInteractionEvent(CourseReview review, UserInteraction.Type interactionType) {
        super(review, interactionType);
    }
}
