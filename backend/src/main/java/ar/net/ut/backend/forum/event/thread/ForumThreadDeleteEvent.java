package ar.net.ut.backend.forum.event.thread;

import ar.net.ut.backend.forum.ForumThread;
import ar.net.ut.backend.log.Log;

public class ForumThreadDeleteEvent extends ForumThreadEvent {

    public ForumThreadDeleteEvent(ForumThread forumThread) {
        super(forumThread, Log.Action.DELETE);
    }
}
