package ar.net.ut.backend.subject.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record SubjectCreateDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name is too long")
        String name,

        @NotBlank(message = "Short name is required")
        @Size(max = 3, message = "Short name is too long")
        String shortName,

        @NotEmpty(message = "Careers are required")
        List<Long> careerIds,

        List<Long> correlativeIds
) {
}
