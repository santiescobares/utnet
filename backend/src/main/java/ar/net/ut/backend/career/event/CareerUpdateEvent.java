package ar.net.ut.backend.career.event;

import ar.net.ut.backend.career.Career;

public class CareerUpdateEvent extends CareerEvent {

    public CareerUpdateEvent(Career career) {
        super(career);
    }
}
