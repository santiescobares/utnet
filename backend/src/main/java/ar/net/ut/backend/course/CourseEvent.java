package ar.net.ut.backend.course;

import ar.net.ut.backend.model.loggable.CUDLoggableEntity;
import ar.net.ut.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "course_events")
@Getter
@Setter
@SQLDelete(sql = "UPDATE course_events SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class CourseEvent extends CUDLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private LocalDate date;
    private LocalTime startTime, endTime;

    @Column(length = 500)
    private String description;
    @Enumerated(EnumType.STRING)
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_editor_id")
    private User lastEditor;

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"courseId\":" + (course != null ? course.getId() : null) +
                ", \"date\":\"" + date + "\"" +
                ", \"startTime\":\"" + startTime + "\"" +
                ", \"endTime\":\"" + endTime + "\"" +
                ", \"description\":\"" + (description != null ? description.replace("\"", "\\\"") : null) + "\"" +
                ", \"tag\":\"" + tag + "\"" +
                ", \"createdById\":\"" + (createdBy != null ? createdBy.getId() : null) + "\"" +
                ", \"lastEditorId\":\"" + (lastEditor != null ? lastEditor.getId() : null) + "\"" +
                ", \"createdAt\":\"" + getCreatedAt() + "\"" +
                ", \"updatedAt\":\"" + getUpdatedAt() + "\"" +
                ", \"deletedAt\":\"" + getDeletedAt() + "\"" +
                "}";
    }

    @Getter
    @AllArgsConstructor
    public enum Tag {
        MIDTERM_EXAM("B4E600"),
        REMEDIAL_EXAM("E68D00"),
        FINAL_EXAM("E60400"),
        PRACTICAL_WORK("00E2E6"),
        QUESTIONNAIRE("BB00E6"),
        SPECIAL_LESSON("3200E6"),
        SPECIAL_DAY("0058E6"),
        OTHER("505250");

        private final String color;
    }
}
