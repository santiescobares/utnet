package ar.net.ut.backend.course.repository;

import ar.net.ut.backend.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByName(String name);

    boolean existsByCareerId(Long careerId);

    boolean existsByCareerIdAndYearAndDivision(Long careerId, int year, int division);

    boolean existsByCareerIdAndYearAndDivisionAndIdNot(Long careerId, int year, int division, Long id);

    List<Course> findAllByCareerId(Long careerId);
}
