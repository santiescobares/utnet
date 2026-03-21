package ar.net.ut.backend.forum.event.thread;

import ar.net.ut.backend.forum.ForumThread;
import ar.net.ut.backend.log.Log;

public class ForumThreadUpdateEvent extends ForumThreadEvent {

    public ForumThreadUpdateEvent(ForumThread forumThread) {
        super(forumThread, Log.Action.EDIT);
    }
}
