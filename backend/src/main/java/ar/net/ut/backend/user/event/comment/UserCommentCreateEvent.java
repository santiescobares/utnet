package ar.net.ut.backend.user.event.comment;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.UserComment;

public class UserCommentCreateEvent extends UserCommentEvent {

    public UserCommentCreateEvent(UserComment comment) {
        super(comment, Log.Action.CREATE);
    }
}
