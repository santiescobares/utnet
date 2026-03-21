package ar.net.ut.backend.forum.event.thread;

import ar.net.ut.backend.forum.ForumThread;
import ar.net.ut.backend.log.Log;

public class ForumThreadCreateEvent extends ForumThreadEvent {

    public ForumThreadCreateEvent(ForumThread forumThread) {
        super(forumThread, Log.Action.CREATE);
    }
}
