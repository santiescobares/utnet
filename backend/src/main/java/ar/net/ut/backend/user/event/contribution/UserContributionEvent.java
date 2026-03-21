package ar.net.ut.backend.user.event.contribution;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.user.UserContribution;
import lombok.Getter;

@Getter
public abstract class UserContributionEvent extends LoggableEvent<UserContribution> {

    private final UserContribution contribution;

    public UserContributionEvent(UserContribution contribution, Log.Action action) {
        super(contribution.getUser(), ResourceType.USER_CONTRIBUTION, contribution.getId().toString(), action);
        this.contribution = contribution;
    }

    @Override
    public UserContribution getEntity() { return contribution; }
}
