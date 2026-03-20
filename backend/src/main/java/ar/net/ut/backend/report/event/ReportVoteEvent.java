package ar.net.ut.backend.report.event;

import ar.net.ut.backend.report.Report;
import ar.net.ut.backend.user.User;
import lombok.Getter;

@Getter
public class ReportVoteEvent extends ReportEvent {

    private final User voter;

    public ReportVoteEvent(Report report, User voter) {
        super(report);
        this.voter = voter;
    }
}
