package ar.net.ut.backend.report.dto;

import ar.net.ut.backend.report.Report;
import jakarta.validation.constraints.NotNull;

public record ReportResolveDTO(
        @NotNull(message = "Resolution is required")
        Report.Status resolution
) {
}
