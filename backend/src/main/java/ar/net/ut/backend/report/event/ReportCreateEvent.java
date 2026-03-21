package ar.net.ut.backend.report.event;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.report.Report;

public class ReportCreateEvent extends ReportEvent {

    public ReportCreateEvent(Report report) {
        super(report, Log.Action.CREATE);
    }
}
