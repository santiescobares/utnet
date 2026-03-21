package ar.net.ut.backend.punishment.event;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.punishment.Punishment;

public class PunishmentDeleteEvent extends PunishmentEvent {

    public PunishmentDeleteEvent(Punishment punishment) {
        super(punishment, Log.Action.DELETE);
    }
}
