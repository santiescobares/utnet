package ar.net.ut.backend.user.event.interaction;

import ar.net.ut.backend.user.UserInteraction;

public class UserInteractionDeleteEvent extends UserInteractionEvent {
    public UserInteractionDeleteEvent(UserInteraction interaction) {
        super(interaction);
    }
}
