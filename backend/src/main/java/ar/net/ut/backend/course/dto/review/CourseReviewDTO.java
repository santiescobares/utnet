package ar.net.ut.backend.course.dto.review;

import java.time.Instant;
import java.util.UUID;

public record CourseReviewDTO(

        Long id,
        Long courseId,
        UUID postedById,
        String content,
        double rating,
        Instant createdAt

) {
}
