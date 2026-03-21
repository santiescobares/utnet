package ar.net.ut.backend.user.event.comment;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.user.UserComment;
import lombok.Getter;

@Getter
public abstract class UserCommentEvent extends LoggableEvent<UserComment> {

    private final UserComment comment;

    public UserCommentEvent(UserComment comment, Log.Action action) {
        super(comment.getPostedBy(), ResourceType.USER_COMMENT, comment.getId().toString(), action);
        this.comment = comment;
    }

    @Override
    public UserComment getEntity() { return comment; }
}
