package ar.net.ut.backend.course.dto.review;

import ar.net.ut.backend.subject.dto.SubjectDTO;
import ar.net.ut.backend.user.dto.UserSnapshotDTO;

import java.time.Instant;
import java.util.Set;

public record CourseReviewDTO(
        Long id,
        Instant createdAt,
        Long courseId,
        UserSnapshotDTO postedBy,
        String content,
        double rating,
        Set<SubjectDTO> subjectTags
) {
}
