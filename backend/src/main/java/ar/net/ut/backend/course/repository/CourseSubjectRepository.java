package ar.net.ut.backend.course.repository;

import ar.net.ut.backend.course.CourseSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseSubjectRepository extends JpaRepository<CourseSubject, Long> {

    boolean existsByCourseIdAndSubjectId(Long courseId, Long subjectId);

    boolean existsByCourseIdAndSubjectIdAndIdNot(Long courseId, Long subjectId, Long id);

    List<CourseSubject> findByCourseId(Long courseId);
}
