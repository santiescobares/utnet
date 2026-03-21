package ar.net.ut.backend.report.event;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.report.Report;
import lombok.Getter;

@Getter
public class ReportVoteEvent extends ReportEvent {

    private final boolean inFavor;

    public ReportVoteEvent(Report report, boolean inFavor) {
        super(report, Log.Action.EDIT);
        this.inFavor = inFavor;
    }
}
