package ar.net.ut.backend.subject.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SubjectUpdateDTO(
        @Size(max = 50, message = "Name is too long")
        String name,

        @Size(max = 3, message = "Short name is too long")
        String shortName,

        @PositiveOrZero(message = "Sort position must be positive or zero")
        Integer sortPosition,

        List<Long> careerIds,

        List<Long> correlativeIds
) {
}
