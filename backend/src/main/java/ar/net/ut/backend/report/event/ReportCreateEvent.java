package ar.net.ut.backend.report.event;

import ar.net.ut.backend.report.Report;

public class ReportCreateEvent extends ReportEvent {

    public ReportCreateEvent(Report report) {
        super(report);
    }
}
