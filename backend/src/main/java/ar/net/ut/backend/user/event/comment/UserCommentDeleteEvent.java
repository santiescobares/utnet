package ar.net.ut.backend.user.event.comment;

import ar.net.ut.backend.user.UserComment;

public class UserCommentDeleteEvent extends UserCommentEvent {
    public UserCommentDeleteEvent(UserComment comment) {
        super(comment);
    }
}
