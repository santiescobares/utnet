package ar.net.ut.backend.course.dto.event;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CourseEventUpdateDTO(

        LocalDate date,

        LocalTime startTime,

        LocalTime endTime,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description

) {
}
