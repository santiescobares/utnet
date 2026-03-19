package ar.net.ut.backend.user.event.comment;

import ar.net.ut.backend.user.entity.UserComment;

public class UserCommentCreateEvent extends UserCommentEvent {
    public UserCommentCreateEvent(UserComment comment) {
        super(comment);
    }
}
