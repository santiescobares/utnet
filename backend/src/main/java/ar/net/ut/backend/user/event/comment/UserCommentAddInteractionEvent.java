package ar.net.ut.backend.user.event.comment;

import ar.net.ut.backend.model.event.CommentAddInteractionEvent;
import ar.net.ut.backend.user.UserComment;
import ar.net.ut.backend.user.UserInteraction;

public class UserCommentAddInteractionEvent extends CommentAddInteractionEvent<UserComment> {

    public UserCommentAddInteractionEvent(UserComment comment, UserInteraction.Type interactionType) {
        super(comment, interactionType);
    }
}
