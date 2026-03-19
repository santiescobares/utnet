package ar.net.ut.backend.user.event.contribution;

import ar.net.ut.backend.user.entity.UserContribution;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class UserContributionEvent {
    private final UserContribution contribution;
}
