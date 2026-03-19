package ar.net.ut.backend.course.repository;

import ar.net.ut.backend.course.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    boolean existsByResourceIdAndPostedById(Long courseId, UUID userId);

    List<CourseReview> findByResourceId(Long courseId);
}
