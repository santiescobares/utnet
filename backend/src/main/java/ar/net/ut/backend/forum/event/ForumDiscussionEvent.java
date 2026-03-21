package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.ForumDiscussion;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ForumDiscussionEvent {

    private final ForumDiscussion forumDiscussion;
}
