package ar.net.ut.backend.report.event;

import ar.net.ut.backend.report.Report;
import lombok.Getter;

@Getter
public class ReportDeclinedEvent extends ReportEvent {

    private final boolean resolvedByAdmin;

    public ReportDeclinedEvent(Report report, boolean resolvedByAdmin) {
        super(report);
        this.resolvedByAdmin = resolvedByAdmin;
    }
}
