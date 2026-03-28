package ar.net.ut.backend.course.dto.event;

import ar.net.ut.backend.course.CourseEvent;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CourseEventCreateDTO(
        @NotNull(message = "Course ID is required")
        Long courseId,

        @NotNull(message = "Date is required")
        @FutureOrPresent(message = "Date must be in the future or present")
        LocalDate date,

        @FutureOrPresent(message = "Start time must be in the future or present")
        LocalTime startTime,

        @FutureOrPresent(message = "End time must be in the future or present")
        LocalTime endTime,

        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description is too long")
        String description,

        @NotNull(message = "Tag is required")
        CourseEvent.Tag tag
) {
}
