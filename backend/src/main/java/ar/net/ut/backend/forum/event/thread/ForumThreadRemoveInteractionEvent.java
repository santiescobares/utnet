package ar.net.ut.backend.forum.event.thread;

import ar.net.ut.backend.forum.ForumThread;
import ar.net.ut.backend.user.UserInteraction;
import lombok.Getter;

@Getter
public class ForumThreadRemoveInteractionEvent extends ForumThreadEvent {

    private final UserInteraction.Type interactionType;

    public ForumThreadRemoveInteractionEvent(ForumThread forumThread, UserInteraction.Type interactionType) {
        super(forumThread);
        this.interactionType = interactionType;
    }
}
