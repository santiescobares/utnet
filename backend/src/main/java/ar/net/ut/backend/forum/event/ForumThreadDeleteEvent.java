package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.ForumThread;

public class ForumThreadDeleteEvent extends ForumThreadEvent {

    public ForumThreadDeleteEvent(ForumThread forumThread) {
        super(forumThread);
    }
}
