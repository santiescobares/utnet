package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.ForumDiscussion;
import ar.net.ut.backend.log.Log;

public class ForumDiscussionDeleteEvent extends ForumDiscussionEvent {

    public ForumDiscussionDeleteEvent(ForumDiscussion forumDiscussion) {
        super(forumDiscussion, Log.Action.DELETE);
    }
}
