package ar.net.ut.backend.studyrecord.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record StudyRecordUpdateDTO(
        @Size(min = 5, max = 100, message = "Title is either too short or too long")
        String title,

        @Size(max = 1000, message = "Description is too long")
        String description,

        List<String> tags,

        Boolean hidden
) {
}
