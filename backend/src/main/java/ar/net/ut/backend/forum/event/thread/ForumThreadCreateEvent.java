package ar.net.ut.backend.forum.event.thread;

import ar.net.ut.backend.forum.ForumThread;

public class ForumThreadCreateEvent extends ForumThreadEvent {

    public ForumThreadCreateEvent(ForumThread forumThread) {
        super(forumThread);
    }
}
