package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.ForumDiscussion;

public class ForumDiscussionUpdateEvent extends ForumDiscussionEvent {

    public ForumDiscussionUpdateEvent(ForumDiscussion forumDiscussion) {
        super(forumDiscussion);
    }
}
