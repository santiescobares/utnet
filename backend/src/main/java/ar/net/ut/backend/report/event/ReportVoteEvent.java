package ar.net.ut.backend.report.event;

import ar.net.ut.backend.report.Report;
import lombok.Getter;

@Getter
public class ReportVoteEvent extends ReportEvent {

    private final boolean inFavor;

    public ReportVoteEvent(Report report, boolean inFavor) {
        super(report);
        this.inFavor = inFavor;
    }
}
