package ar.net.ut.backend.model.event;

import ar.net.ut.backend.model.CommentEntity;
import ar.net.ut.backend.user.UserInteraction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CommentAddInteractionEvent<T extends CommentEntity<?>> {

    private final T entity;
    private final UserInteraction.Type interactionType;
}
