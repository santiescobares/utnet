package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.entity.Forum;

public class ForumDeleteEvent extends ForumEvent {

    public ForumDeleteEvent(Forum forum) {
        super(forum);
    }
}
