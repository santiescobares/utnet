package ar.net.ut.backend.course.repository;

import ar.net.ut.backend.course.CourseReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    boolean existsByResourceIdAndPostedById(Long courseId, UUID userId);

    @EntityGraph(attributePaths = {"postedBy"})
    Page<CourseReview> findByResourceId(Long courseId, Pageable pageable);
}
