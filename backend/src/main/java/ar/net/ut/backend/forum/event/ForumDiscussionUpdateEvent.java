package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.ForumDiscussion;
import ar.net.ut.backend.log.Log;

public class ForumDiscussionUpdateEvent extends ForumDiscussionEvent {

    public ForumDiscussionUpdateEvent(ForumDiscussion forumDiscussion) {
        super(forumDiscussion, Log.Action.EDIT);
    }
}
