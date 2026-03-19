package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.entity.ForumThread;

public class ForumThreadUpdateEvent extends ForumThreadEvent {

    public ForumThreadUpdateEvent(ForumThread forumThread) {
        super(forumThread);
    }
}
