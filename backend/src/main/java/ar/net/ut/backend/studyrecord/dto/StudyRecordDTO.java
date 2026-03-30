package ar.net.ut.backend.studyrecord.dto;

import ar.net.ut.backend.studyrecord.StudyRecord;
import ar.net.ut.backend.subject.dto.SubjectSoftDTO;
import ar.net.ut.backend.user.dto.UserSnapshotDTO;

import java.time.Instant;
import java.util.List;

public record StudyRecordDTO(
        Long id,
        Instant createdAt,
        Instant updatedAt,
        UserSnapshotDTO createdBy,
        String title,
        String slug,
        String description,
        StudyRecord.Type type,
        String typeColor,
        List<SubjectSoftDTO> subjects,
        List<String> tags,
        long resourceSize,
        int downloads,
        boolean hidden
) {
}
