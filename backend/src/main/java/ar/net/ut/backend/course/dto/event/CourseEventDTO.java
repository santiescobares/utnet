package ar.net.ut.backend.course.dto.event;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CourseEventDTO(
        Long id,
        Instant createdAt,
        Instant updatedAt,
        Long courseId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String description,
        UUID createdById,
        UUID lastEditorId
) {
}
