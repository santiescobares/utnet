package ar.net.ut.backend.course.dto.event;

import ar.net.ut.backend.course.CourseEvent;
import ar.net.ut.backend.user.dto.UserSnapshotDTO;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

public record CourseEventDTO(
        Long id,
        Instant createdAt,
        Instant updatedAt,
        Long courseId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String description,
        CourseEvent.Tag tag,
        String tagColor,
        UserSnapshotDTO createdBy,
        UserSnapshotDTO lastEditor
) {
}
