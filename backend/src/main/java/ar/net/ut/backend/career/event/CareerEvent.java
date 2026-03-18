package ar.net.ut.backend.career.event;

import ar.net.ut.backend.career.Career;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CareerEvent {

    private final Career career;
}
