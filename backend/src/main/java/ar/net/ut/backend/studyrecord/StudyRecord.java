package ar.net.ut.backend.studyrecord;

import ar.net.ut.backend.model.loggable.CUDLoggableEntity;
import ar.net.ut.backend.subject.Subject;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.util.StringUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.util.Collections;
import java.util.List;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(length = 100)
    private String title;
    @Column(unique = true)
    private String slug;
    @Column(length = 1000)
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> tags;

    private String resourceKey;

    private int downloads;

    private boolean hidden;

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
}
