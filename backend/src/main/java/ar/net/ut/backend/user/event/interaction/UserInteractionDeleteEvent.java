package ar.net.ut.backend.user.event.interaction;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.UserInteraction;

public class UserInteractionDeleteEvent extends UserInteractionEvent {

    public UserInteractionDeleteEvent(UserInteraction interaction) {
        super(interaction, Log.Action.DELETE);
    }
}
