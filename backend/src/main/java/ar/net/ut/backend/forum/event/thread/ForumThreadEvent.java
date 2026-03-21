package ar.net.ut.backend.forum.event.thread;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.forum.ForumThread;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import lombok.Getter;

@Getter
public abstract class ForumThreadEvent extends LoggableEvent<ForumThread> {

    private final ForumThread forumThread;

    public ForumThreadEvent(ForumThread forumThread, Log.Action action) {
        super(forumThread.getPostedBy(), ResourceType.FORUM_THREAD, forumThread.getId().toString(), action);
        this.forumThread = forumThread;
    }

    @Override
    public ForumThread getEntity() { return forumThread; }
}
