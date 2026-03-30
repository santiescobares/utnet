package ar.net.ut.backend.studyrecord.dto;

import ar.net.ut.backend.studyrecord.StudyRecord;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record StudyRecordCreateDTO(
        @NotBlank(message = "Title is required")
        @Size(min = 5, max = 100, message = "Title is either too short or too long")
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 2000, message = "Description is too long")
        String description,

        @NotNull(message = "Type is required")
        StudyRecord.Type type,

        @NotEmpty(message = "Subjects are required")
        List<Long> subjectIds,

        List<String> tags
) {
}
