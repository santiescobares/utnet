package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.entity.Forum;

public class ForumUpdateEvent extends ForumEvent {

    public ForumUpdateEvent(Forum forum) {
        super(forum);
    }
}
