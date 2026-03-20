package ar.net.ut.backend.studyrecord.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StudyRecordDTO(
        Long id,
        Instant createdAt,
        Instant updatedAt,
        UUID createdById,
        Long subjectId,
        String title,
        String slug,
        String description,
        List<String> tags,
        int downloads,
        boolean hidden,
        boolean hasFile
) {
}
