package ar.net.ut.backend.forum.event.thread;

import ar.net.ut.backend.forum.ForumThread;

public class ForumThreadUpdateEvent extends ForumThreadEvent {

    public ForumThreadUpdateEvent(ForumThread forumThread) {
        super(forumThread);
    }
}
