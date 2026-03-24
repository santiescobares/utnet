package ar.net.ut.backend.course.dto.review;

import ar.net.ut.backend.user.dto.UserSnapshotDTO;

import java.time.Instant;

public record CourseReviewDTO(
        Long id,
        Instant createdAt,
        Long courseId,
        UserSnapshotDTO postedBy,
        String content,
        double rating
) {
}
