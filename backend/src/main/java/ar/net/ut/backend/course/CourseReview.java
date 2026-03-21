package ar.net.ut.backend.course;

import ar.net.ut.backend.model.CommentEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

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
