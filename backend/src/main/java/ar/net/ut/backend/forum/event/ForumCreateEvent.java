package ar.net.ut.backend.forum.event;

import ar.net.ut.backend.forum.Forum;

public class ForumCreateEvent extends ForumEvent {

    public ForumCreateEvent(Forum forum) {
        super(forum);
    }
}
