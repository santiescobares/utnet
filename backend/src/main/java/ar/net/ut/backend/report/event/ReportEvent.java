package ar.net.ut.backend.report.event;

import ar.net.ut.backend.report.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class ReportEvent {

    private final Report report;
}
