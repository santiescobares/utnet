package ar.net.ut.backend.log.event;

import ar.net.ut.backend.log.Log;

public class LogCreateEvent extends LogEvent {
    public LogCreateEvent(Log log) {
        super(log);
    }
}
