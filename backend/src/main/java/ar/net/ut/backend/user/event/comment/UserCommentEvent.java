package ar.net.ut.backend.user.event.comment;

import ar.net.ut.backend.user.UserComment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class UserCommentEvent {

    private final UserComment comment;
}
