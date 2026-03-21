package ar.net.ut.backend.punishment.event;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.punishment.Punishment;

public class PunishmentCreateEvent extends PunishmentEvent {

    public PunishmentCreateEvent(Punishment punishment) {
        super(punishment, Log.Action.CREATE);
    }
}
