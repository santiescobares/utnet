package ar.net.ut.backend.report.dto;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.report.Report;

import java.time.Instant;
import java.util.UUID;

public record ReportDTO(
        Long id,
        Instant createdAt,
        UUID reporterId,
        ResourceType resourceType,
        String resourceId,
        String reason,
        Report.Status status,
        int votesInFavor,
        int votesAgainst,
        int requiredVotes
) {
}
