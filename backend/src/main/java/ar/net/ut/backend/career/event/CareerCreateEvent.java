package ar.net.ut.backend.career.event;

import ar.net.ut.backend.career.Career;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.user.User;

public class CareerCreateEvent extends CareerEvent {

    public CareerCreateEvent(User user, Career career) {
        super(user, career, Log.Action.CREATE);
    }
}
