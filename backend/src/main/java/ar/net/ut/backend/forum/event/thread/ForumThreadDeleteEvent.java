package ar.net.ut.backend.forum.event.thread;

import ar.net.ut.backend.forum.ForumThread;

public class ForumThreadDeleteEvent extends ForumThreadEvent {

    public ForumThreadDeleteEvent(ForumThread forumThread) {
        super(forumThread);
    }
}
