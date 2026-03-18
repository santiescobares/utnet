package ar.net.ut.backend.career.event;

import ar.net.ut.backend.career.Career;

public class CareerDeleteEvent extends CareerEvent {

    public CareerDeleteEvent(Career career) {
        super(career);
    }
}
