package ar.net.ut.backend.course.dto.subject;

import ar.net.ut.backend.enums.ProfessorPosition;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.util.Map;

public record CourseSubjectCreateDTO(
        @NotNull(message = "Course ID is required")
        Long courseId,

        @NotNull(message = "Subject ID is required")
        Long subjectId,

        @NotNull(message = "Professors are required")
        Map<ProfessorPosition, String> professors,

        @NotNull(message = "Class days are required")
        Map<DayOfWeek, String> classDays
) {
}
