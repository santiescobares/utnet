package ar.net.ut.backend.course;

import ar.net.ut.backend.model.CommentEntity;
import ar.net.ut.backend.subject.Subject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "course_reviews")
@Getter
@Setter
@SQLDelete(sql = "UPDATE course_reviews SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class CourseReview extends CommentEntity<Course> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double rating;

    @ManyToMany
    @JoinTable(
            name = "course_review_tags",
            joinColumns = @JoinColumn(name = "review_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    @BatchSize(size = 3)
    private Set<Subject> subjectTags;

    public boolean addSubjectTag(Subject subject) {
        if (subjectTags == null) {
            subjectTags = new HashSet<>();
        }
        return subjectTags.add(subject);
    }

    public boolean removeSubjectTag(Subject subject) {
        return subjectTags.remove(subject);
    }

    public Set<Subject> getSubjectTags() {
        return subjectTags != null ? Collections.unmodifiableSet(subjectTags) : Collections.emptySet();
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ", \"resourceId\":" + (getResource() != null ? getResource().getId() : null) +
                ", \"postedById\":\"" + (getPostedBy() != null ? getPostedBy().getId() : null) + "\"" +
                ", \"content\":\"" + (getContent() != null ? getContent().replace("\"", "\\\"") : null) + "\"" +
                ", \"likes\":" + getLikes() +
                ", \"dislikes\":" + getDislikes() +
                ", \"rating\":" + rating +
                ", \"createdAt\":\"" + getCreatedAt() + "\"" +
                ", \"deletedAt\":\"" + getDeletedAt() + "\"" +
                "}";
    }
}
