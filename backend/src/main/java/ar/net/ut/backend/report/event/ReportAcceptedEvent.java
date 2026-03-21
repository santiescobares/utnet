package ar.net.ut.backend.report.event;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.report.Report;
import lombok.Getter;

@Getter
public class ReportAcceptedEvent extends ReportEvent {

    private final boolean resolvedByAdmin;

    public ReportAcceptedEvent(Report report, boolean resolvedByAdmin) {
        super(report, Log.Action.EDIT);
        this.resolvedByAdmin = resolvedByAdmin;
    }
}
