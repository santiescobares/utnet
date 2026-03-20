package ar.net.ut.backend.user.event.contribution;

import ar.net.ut.backend.user.UserContribution;

import java.util.List;

public record UserContributionBulkCreateEvent(
        List<UserContribution> contributions
) {
}
