package ar.net.ut.backend.user.listener;

import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.event.comment.UserCommentAddInteractionEvent;
import ar.net.ut.backend.user.event.comment.UserCommentRemoveInteractionEvent;
import ar.net.ut.backend.user.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCommentListener {

    private final UserInteractionService userInteractionService;

    @EventListener
    public void onUserCommentAddInteraction(UserCommentAddInteractionEvent event) {
        userInteractionService.createInteraction(
                RequestContextHolder.getCurrentSession().userId(),
                event.getInteractionType(),
                ResourceType.USER_COMMENT,
                event.getEntity().getId().toString()
        );
    }

    @EventListener
    public void onUserCommentRemoveInteraction(UserCommentRemoveInteractionEvent event) {
        userInteractionService.deleteInteraction(
                RequestContextHolder.getCurrentSession().userId(),
                event.getInteractionType(),
                ResourceType.USER_COMMENT,
                event.getEntity().getId().toString()
        );
    }
}
