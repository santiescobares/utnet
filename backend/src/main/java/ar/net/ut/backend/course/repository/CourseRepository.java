package ar.net.ut.backend.course.repository;

import ar.net.ut.backend.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByCareerId(Long careerId);

    boolean existsByCareerIdAndYearAndDivision(Long careerId, int year, int division);

    boolean existsByCareerIdAndYearAndDivisionAndIdNot(Long careerId, int year, int division, Long id);

    List<Course> findByCareerId(Long careerId);
}
