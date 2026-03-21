package ar.net.ut.backend.report.event;

import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.report.Report;

public class ReportDeleteEvent extends ReportEvent {

    public ReportDeleteEvent(Report report) {
        super(report, Log.Action.DELETE);
    }
}
