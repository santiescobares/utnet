package ar.net.ut.backend.course.dto.subject;

import ar.net.ut.backend.enums.ProfessorPosition;

import java.time.DayOfWeek;
import java.util.Map;

public record CourseSubjectUpdateDTO(
        Map<ProfessorPosition, String> professors,

        Map<DayOfWeek, String> classDays
) {
}
