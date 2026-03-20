package ar.net.ut.backend.report.event;

import ar.net.ut.backend.report.Report;

public class ReportAcceptedEvent extends ReportEvent {

    public ReportAcceptedEvent(Report report) {
        super(report);
    }
}
