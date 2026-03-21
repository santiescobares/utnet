package ar.net.ut.backend.studyrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record StudyRecordCreateDTO(
        @NotNull(message = "Subject ID is required")
        Long subjectId,

        @NotBlank(message = "Title is required")
        @Size(min = 5, max = 100, message = "Title is either too short or too long")
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description is too long")
        String description,

        List<String> tags
) {
}
