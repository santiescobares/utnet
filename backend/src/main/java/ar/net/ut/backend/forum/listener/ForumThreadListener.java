package ar.net.ut.backend.forum.listener;

import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.forum.event.thread.ForumThreadAddInteractionEvent;
import ar.net.ut.backend.forum.event.thread.ForumThreadRemoveInteractionEvent;
import ar.net.ut.backend.user.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ForumThreadListener {

    private final UserInteractionService userInteractionService;

    @EventListener
    public void onForumThreadAddInteraction(ForumThreadAddInteractionEvent event) {
        userInteractionService.createInteraction(
                RequestContextHolder.getCurrentSession().userId(),
                event.getInteractionType(),
                ResourceType.FORUM_THREAD,
                event.getForumThread().getId().toString()
        );
    }

    @EventListener
    public void onForumThreadRemoveInteraction(ForumThreadRemoveInteractionEvent event) {
        userInteractionService.deleteInteraction(
                RequestContextHolder.getCurrentSession().userId(),
                event.getInteractionType(),
                ResourceType.FORUM_THREAD,
                event.getForumThread().getId().toString()
        );
    }
}
