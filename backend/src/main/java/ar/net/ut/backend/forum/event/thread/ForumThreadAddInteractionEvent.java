package ar.net.ut.backend.forum.event.thread;

import ar.net.ut.backend.forum.ForumThread;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.UserInteraction;
import lombok.Getter;

@Getter
public class ForumThreadAddInteractionEvent extends ForumThreadEvent {

    private final UserInteraction.Type interactionType;

    public ForumThreadAddInteractionEvent(ForumThread forumThread, UserInteraction.Type interactionType) {
        super(forumThread, Log.Action.EDIT);
        this.interactionType = interactionType;
    }
}
