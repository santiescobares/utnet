package ar.net.ut.backend.course.repository;

import ar.net.ut.backend.course.CourseEvent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CourseEventRepository extends JpaRepository<CourseEvent, Long> {

    @EntityGraph(attributePaths = {"createdBy", "lastEditor"})
    List<CourseEvent> findByCourseId(Long courseId);

    @EntityGraph(attributePaths = {"createdBy", "lastEditor"})
    List<CourseEvent> findByCourseIdAndDateBetween(Long courseId, LocalDate from, LocalDate to);
}
