package ar.net.ut.backend.user.event.interaction;

import ar.net.ut.backend.user.UserInteraction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class UserInteractionEvent {
    private final UserInteraction interaction;
}
