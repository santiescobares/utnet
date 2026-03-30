package ar.net.ut.backend.studyrecord;

import ar.net.ut.backend.model.loggable.CUDLoggableEntity;
import ar.net.ut.backend.subject.Subject;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.util.StringUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "study_records")
@Getter
@Setter
@SQLDelete(sql = "UPDATE study_records SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class StudyRecord extends CUDLoggableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(length = 100)
    private String title;
    @Column(unique = true)
    private String slug;
    @Column(length = 2000)
    private String description;
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToMany
    @JoinTable(
            name = "study_record_subjects",
            joinColumns = @JoinColumn(name = "study_record_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    @BatchSize(size = 5)
    private Set<Subject> subjects;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> tags;

    private String resourceKey;
    private long resourceSize;

    private int downloads;

    private boolean hidden;

    public boolean addSubject(Subject subject) {
        if (subjects == null) {
            subjects = new HashSet<>();
        }
        return subjects.add(subject);
    }

    public boolean removeSubject(Subject subject) {
        return subjects.remove(subject);
    }

    public Set<Subject> getSubjects() {
        return subjects != null ? Collections.unmodifiableSet(subjects) : Collections.emptySet();
    }

    public void setTags(List<String> tags) {
        if (tags != null) {
            this.tags = tags.stream()
                    .filter(tag -> {
                        if (tag.contains(" ")) {
                            throw new IllegalArgumentException("Tag must be a single word");
                        }
                        return true;
                    })
                    .map(StringUtil::normalize)
                    .toList();
        } else {
            this.tags = null;
        }
    }

    public List<String> getTags() {
        return tags != null ? Collections.unmodifiableList(tags) : Collections.emptyList();
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"createdById\":\"" + (createdBy != null ? createdBy.getId() : null) + "\"" +
                ", \"title\":\"" + (title != null ? title.replace("\"", "\\\"") : null) + "\"" +
                ", \"slug\":\"" + (slug != null ? slug.replace("\"", "\\\"") : null) + "\"" +
                ", \"description\":\"" + (description != null ? description.replace("\"", "\\\"") : null) + "\"" +
                ", \"subjects\":" + subjects +
                ", \"tags\":" + tags +
                ", \"resourceKey\":\"" + resourceKey + "\"" +
                ", \"downloads\":" + downloads +
                ", \"hidden\":" + hidden +
                ", \"createdAt\":\"" + getCreatedAt() + "\"" +
                ", \"updatedAt\":\"" + getUpdatedAt() + "\"" +
                ", \"deletedAt\":\"" + getDeletedAt() + "\"" +
                "}";
    }

    @Getter
    @AllArgsConstructor
    public enum Type {
        SUMMARY("0058E6"),
        NOTE("00E2E6"),
        BIBLIOGRAPHY("E60086"),
        EXAM_MODEL("E68D00");

        private final String color;
    }
}
