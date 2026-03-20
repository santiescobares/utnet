package ar.net.ut.backend.report;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.report.event.ReportAcceptedEvent;
import ar.net.ut.backend.report.event.ReportVoteEvent;
import ar.net.ut.backend.user.UserContribution;
import ar.net.ut.backend.user.event.contribution.UserContributionCreateEvent;
import ar.net.ut.backend.user.repository.UserContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportListener {

    private static final int VOTE_POINTS = 5;
    private static final int REPORT_ACCEPTED_POINTS = 2;

    private final UserContributionRepository contributionRepository;
    private final ApplicationEventPublisher eventPublisher;

    @EventListener
    public void awardVoterPoints(ReportVoteEvent event) {
        UserContribution contribution = new UserContribution();
        contribution.setUser(event.getVoter());
        contribution.setResourceType(ResourceType.REPORT);
        contribution.setResourceId(event.getReport().getId().toString());
        contribution.setAwardedPoints(VOTE_POINTS);

        contributionRepository.save(contribution);
        eventPublisher.publishEvent(new UserContributionCreateEvent(contribution));
    }

    @EventListener
    public void awardReporterPoints(ReportAcceptedEvent event) {
        UserContribution contribution = new UserContribution();
        contribution.setUser(event.getReport().getReporter());
        contribution.setResourceType(ResourceType.REPORT);
        contribution.setResourceId(event.getReport().getId().toString());
        contribution.setAwardedPoints(REPORT_ACCEPTED_POINTS);

        contributionRepository.save(contribution);
        eventPublisher.publishEvent(new UserContributionCreateEvent(contribution));
    }
}
