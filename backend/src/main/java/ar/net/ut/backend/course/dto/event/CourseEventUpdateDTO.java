package ar.net.ut.backend.course.dto.event;

import ar.net.ut.backend.course.CourseEvent;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CourseEventUpdateDTO(
        @FutureOrPresent(message = "Date must be in the future or present")
        LocalDate date,

        @FutureOrPresent(message = "Start time must be in the future or present")
        LocalTime startTime,

        @FutureOrPresent(message = "End time must be in the future or present")
        LocalTime endTime,

        @Size(max = 500, message = "Description is too long")
        String description,

        CourseEvent.Tag tag
) {
}
