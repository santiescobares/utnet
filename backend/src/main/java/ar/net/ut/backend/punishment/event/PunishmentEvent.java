package ar.net.ut.backend.punishment.event;

import ar.net.ut.backend.punishment.Punishment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class PunishmentEvent {

    private final Punishment punishment;
}
