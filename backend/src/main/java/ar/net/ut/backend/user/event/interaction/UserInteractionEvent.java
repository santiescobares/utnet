package ar.net.ut.backend.user.event.interaction;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.user.UserInteraction;
import lombok.Getter;

@Getter
public abstract class UserInteractionEvent extends LoggableEvent<UserInteraction> {

    private final UserInteraction interaction;

    public UserInteractionEvent(UserInteraction interaction, Log.Action action) {
        super(interaction.getUser(), ResourceType.USER_INTERACTION, interaction.getId().toString(), action);
        this.interaction = interaction;
    }

    @Override
    public UserInteraction getEntity() { return interaction; }
}
