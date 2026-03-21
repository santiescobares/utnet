package ar.net.ut.backend.report.event;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import ar.net.ut.backend.report.Report;
import lombok.Getter;

@Getter
public abstract class ReportEvent extends LoggableEvent<Report> {

    private final Report report;

    public ReportEvent(Report report, Log.Action action) {
        super(report.getReporter(), ResourceType.REPORT, report.getId().toString(), action);
        this.report = report;
    }

    @Override
    public Report getEntity() { return report; }
}
