package ar.net.ut.backend.course.dto;

import java.time.Instant;

public record CourseDTO(

        Long id,
        Long careerId,
        int year,
        int division,
        String name,
        Instant createdAt,
        Instant updatedAt

) {
}
