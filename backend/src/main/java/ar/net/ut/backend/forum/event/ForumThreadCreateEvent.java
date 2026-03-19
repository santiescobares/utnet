package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.entity.ForumThread;

public class ForumThreadCreateEvent extends ForumThreadEvent {

    public ForumThreadCreateEvent(ForumThread forumThread) {
        super(forumThread);
    }
}
