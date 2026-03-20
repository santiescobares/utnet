package ar.net.ut.backend.log.event;

import ar.net.ut.backend.log.Log;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class LogEvent {
    private final Log log;
}
