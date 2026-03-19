package ar.net.ut.backend.course.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CourseUpdateDTO(

        Long careerId,

        @Positive(message = "Year must be a positive number")
        Integer year,

        @PositiveOrZero(message = "Division must be zero or a positive number")
        Integer division

) {
}
