package ar.net.ut.backend.course.dto.subject;

import ar.net.ut.backend.enums.ProfessorPosition;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Map;

public record CourseSubjectDTO(

        Long id,
        Long courseId,
        Long subjectId,
        Map<ProfessorPosition, String> professors,
        Map<DayOfWeek, String> classDays,
        Instant createdAt,
        Instant updatedAt

) {
}
