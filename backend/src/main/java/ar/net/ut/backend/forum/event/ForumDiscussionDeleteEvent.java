package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.ForumDiscussion;

public class ForumDiscussionDeleteEvent extends ForumDiscussionEvent {

    public ForumDiscussionDeleteEvent(ForumDiscussion forumDiscussion) {
        super(forumDiscussion);
    }
}
