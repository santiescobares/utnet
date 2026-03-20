package ar.net.ut.backend.user.event.contribution;

import ar.net.ut.backend.user.UserContribution;

public class UserContributionDeleteEvent extends UserContributionEvent {

    public UserContributionDeleteEvent(UserContribution contribution) {
        super(contribution);
    }
}
