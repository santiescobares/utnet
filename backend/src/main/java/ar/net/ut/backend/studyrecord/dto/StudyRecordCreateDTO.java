package ar.net.ut.backend.studyrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record StudyRecordCreateDTO(
        @NotNull(message = "El ID de la materia es obligatorio")
        Long subjectId,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 100, message = "El título no puede superar los 100 caracteres")
        String title,

        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
        String description,

        List<@NotBlank(message = "Los tags no pueden estar vacíos") String> tags
) {
}
