package ar.net.ut.backend.course.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CourseCreateDTO(
        @NotNull(message = "Career ID is required")
        Long careerId,

        @Positive(message = "Year must be a positive number")
        int year,

        @PositiveOrZero(message = "Division must be zero or a positive number")
        int division
) {
}
