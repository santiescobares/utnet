package ar.net.ut.backend.course.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CourseUpdateDTO(
        Long careerId,

        @Positive(message = "Year must be positive")
        Integer year,

        @PositiveOrZero(message = "Division must be positive or zero")
        Integer division
) {
}
