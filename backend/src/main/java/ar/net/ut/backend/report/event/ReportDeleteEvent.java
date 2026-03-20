package ar.net.ut.backend.report.event;

import ar.net.ut.backend.report.Report;

public class ReportDeleteEvent extends ReportEvent {

    public ReportDeleteEvent(Report report) {
        super(report);
    }
}
