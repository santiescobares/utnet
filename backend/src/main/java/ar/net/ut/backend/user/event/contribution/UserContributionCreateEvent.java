package ar.net.ut.backend.user.event.contribution;

import ar.net.ut.backend.user.entity.UserContribution;

public class UserContributionCreateEvent extends UserContributionEvent {
    public UserContributionCreateEvent(UserContribution contribution) {
        super(contribution);
    }
}
