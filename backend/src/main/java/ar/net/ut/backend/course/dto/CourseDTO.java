package ar.net.ut.backend.course.dto;

import java.time.Instant;

public record CourseDTO(
        Long id,
        Instant createdAt,
        Instant updatedAt,
        Long careerId,
        int year,
        int division,
        String name
) {
}
