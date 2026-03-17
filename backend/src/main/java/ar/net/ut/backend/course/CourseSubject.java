package ar.net.ut.backend.course;

import ar.net.ut.backend.enums.ProfessorPosition;
import ar.net.ut.backend.model.loggable.CUDLoggableEntity;
import ar.net.ut.backend.subject.Subject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Map;

@Entity
@Table(name = "course_subjects")
@Getter
@Setter
@SQLDelete(sql = "UPDATE course_subjects SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class CourseSubject extends CUDLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<ProfessorPosition, String> professors;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<DayOfWeek, String> classDays;

    public Map<ProfessorPosition, String> getProfessors() {
        return professors != null ? Collections.unmodifiableMap(professors) : Collections.emptyMap();
    }

    public Map<DayOfWeek, String> getClassDays() {
        return classDays != null ? Collections.unmodifiableMap(classDays) : Collections.emptyMap();
    }
}
