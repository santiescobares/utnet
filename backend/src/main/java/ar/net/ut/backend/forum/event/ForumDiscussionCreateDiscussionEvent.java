package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.ForumDiscussion;
import ar.net.ut.backend.log.Log;

public class ForumDiscussionCreateDiscussionEvent extends ForumDiscussionEvent {

    public ForumDiscussionCreateDiscussionEvent(ForumDiscussion forumDiscussion) {
        super(forumDiscussion, Log.Action.CREATE);
    }
}
