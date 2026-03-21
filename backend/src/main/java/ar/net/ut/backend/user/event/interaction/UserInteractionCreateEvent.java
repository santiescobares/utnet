package ar.net.ut.backend.user.event.interaction;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.UserInteraction;

public class UserInteractionCreateEvent extends UserInteractionEvent {

    public UserInteractionCreateEvent(UserInteraction interaction) {
        super(interaction, Log.Action.CREATE);
    }
}
