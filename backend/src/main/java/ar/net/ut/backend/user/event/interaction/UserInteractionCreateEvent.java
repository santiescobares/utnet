package ar.net.ut.backend.user.event.interaction;

import ar.net.ut.backend.user.UserInteraction;

public class UserInteractionCreateEvent extends UserInteractionEvent {
    public UserInteractionCreateEvent(UserInteraction interaction) {
        super(interaction);
    }
}
