package ar.net.ut.backend.studyrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record StudyRecordUpdateDTO(
        @Size(max = 100, message = "El título no puede superar los 100 caracteres")
        String title,

        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
        String description,

        List<@NotBlank(message = "Los tags no pueden estar vacíos") String> tags,

        Boolean hidden
) {
}
