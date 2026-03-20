package ar.net.ut.backend.report;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.report.event.ReportAcceptedEvent;
import ar.net.ut.backend.report.event.ReportDeclinedEvent;
import ar.net.ut.backend.report.event.ReportEvent;
import ar.net.ut.backend.report.event.ReportVoteEvent;
import ar.net.ut.backend.user.UserInteraction;
import ar.net.ut.backend.user.repository.UserInteractionRepository;
import ar.net.ut.backend.user.service.UserContributionService;
import ar.net.ut.backend.user.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static ar.net.ut.backend.Global.Contributions.*;

@Component
@RequiredArgsConstructor
public class ReportListener {

    private final UserInteractionService userInteractionService;
    private final UserContributionService userContributionService;

    private final UserInteractionRepository userInteractionRepository;

    @EventListener
    public void createReportVoteInteraction(ReportVoteEvent event) {
        userInteractionService.createInteraction(
                RequestContextHolder.getCurrentSession().userId(),
                event.isInFavor() ? UserInteraction.Type.VOTED_IN_FAVOR : UserInteraction.Type.VOTED_AGAINST,
                ResourceType.REPORT,
                event.getReport().getId().toString()
        );
    }

    @EventListener
    public void handleAcceptedReportContributionPoints(ReportAcceptedEvent event) {
        awardResolvedReportPoints(event);

        Report report = event.getReport();
        userContributionService.createContribution(
                report.getReporter().getId(),
                ResourceType.REPORT,
                report.getId().toString(),
                REPORT_RESOLUTION.getPoints() - CONTENT_REPORT.getPoints()
        );
    }

    @EventListener
    public void handleDeclinedReportContributionPoints(ReportDeclinedEvent event) {
        awardResolvedReportPoints(event);

        Report report = event.getReport();
        userContributionService.deleteContribution(report.getReporter().getId(), ResourceType.REPORT, report.getId().toString());
    }

    private void awardResolvedReportPoints(ReportEvent reportEvent) {
        String reportId = reportEvent.getReport().getId().toString();
        List<UUID> voters = userInteractionRepository.findAllInteractorsOn(ResourceType.REPORT, reportId);

        if (!voters.isEmpty()) {
            userContributionService.bulkCreateContributions(voters, ResourceType.REPORT, reportId, REPORT_RESOLUTION.getPoints());
        }
    }
}
