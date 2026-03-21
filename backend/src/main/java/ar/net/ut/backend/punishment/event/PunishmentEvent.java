package ar.net.ut.backend.punishment.event;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.punishment.Punishment;
import lombok.Getter;

@Getter
public abstract class PunishmentEvent extends LoggableEvent<Punishment> {

    private final Punishment punishment;

    public PunishmentEvent(Punishment punishment, Log.Action action) {
        super(punishment.getPunishedBy(), ResourceType.PUNISHMENT, punishment.getId().toString(), action);
        this.punishment = punishment;
    }

    @Override
    public Punishment getEntity() { return punishment; }
}
