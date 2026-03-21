package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.ForumDiscussion;

public class ForumDiscussionCreateDiscussionEvent extends ForumDiscussionEvent {

    public ForumDiscussionCreateDiscussionEvent(ForumDiscussion forumDiscussion) {
        super(forumDiscussion);
    }
}
