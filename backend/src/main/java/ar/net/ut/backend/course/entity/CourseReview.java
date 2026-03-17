package ar.net.ut.backend.course.entity;

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
}
