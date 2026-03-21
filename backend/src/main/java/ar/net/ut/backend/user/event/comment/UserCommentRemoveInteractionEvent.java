package ar.net.ut.backend.user.event.comment;

import ar.net.ut.backend.model.event.CommentRemoveInteractionEvent;
import ar.net.ut.backend.user.UserComment;
import ar.net.ut.backend.user.UserInteraction;

public class UserCommentRemoveInteractionEvent extends CommentRemoveInteractionEvent<UserComment> {

    public UserCommentRemoveInteractionEvent(UserComment comment, UserInteraction.Type interactionType) {
        super(comment, interactionType);
    }
}
