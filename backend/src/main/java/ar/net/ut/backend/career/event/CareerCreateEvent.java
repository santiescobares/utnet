package ar.net.ut.backend.career.event;

import ar.net.ut.backend.career.Career;

public class CareerCreateEvent extends CareerEvent {

    public CareerCreateEvent(Career career) {
        super(career);
    }
}
