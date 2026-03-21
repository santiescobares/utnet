package ar.net.ut.backend.course.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CourseCreateDTO(
        @NotNull(message = "Career ID is required")
        Long careerId,

        @Positive(message = "Year must be positive")
        int year,

        @PositiveOrZero(message = "Division must be positive or zero")
        int division
) {
}
