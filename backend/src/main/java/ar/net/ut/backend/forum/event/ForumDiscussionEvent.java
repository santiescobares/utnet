package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.forum.ForumDiscussion;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import lombok.Getter;

@Getter
public abstract class ForumDiscussionEvent extends LoggableEvent<ForumDiscussion> {

    private final ForumDiscussion forumDiscussion;

    public ForumDiscussionEvent(ForumDiscussion forumDiscussion, Log.Action action) {
        super(forumDiscussion.getCreatedBy(), ResourceType.FORUM_DISCUSSION, forumDiscussion.getId().toString(), action);
        this.forumDiscussion = forumDiscussion;
    }

    @Override
    public ForumDiscussion getEntity() { return forumDiscussion; }
}
