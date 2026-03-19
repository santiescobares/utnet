package ar.net.ut.backend.course.service;

import ar.net.ut.backend.career.Career;
import ar.net.ut.backend.career.CareerService;
import ar.net.ut.backend.course.mapper.CourseMapper;
import ar.net.ut.backend.course.repository.CourseRepository;
import ar.net.ut.backend.course.dto.CourseCreateDTO;
import ar.net.ut.backend.course.dto.CourseDTO;
import ar.net.ut.backend.course.dto.CourseUpdateDTO;
import ar.net.ut.backend.course.Course;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceAlreadyExistsException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CareerService careerService;

    private final CourseRepository courseRepository;

    private final CourseMapper courseMapper;

    @Transactional
    public CourseDTO createCourse(CourseCreateDTO dto) {
        Long careerId = dto.careerId();
        int year = dto.year();
        int division = dto.division();

        if (courseRepository.existsByCareerIdAndYearAndDivision(careerId, year, division)) {
            throw new ResourceAlreadyExistsException(ResourceType.COURSE, "careerId+year+division",
                    careerId + "+" + year + "+" + division);
        }

        Career career = careerService.getById(careerId);
        Course course = courseMapper.createEntity(dto);
        course.setCareer(career);
        course.setName();

        courseRepository.save(course);

        return courseMapper.toDTO(course);
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseUpdateDTO dto) {
        Course course = getById(id);

        Long careerId = dto.careerId() != null ? dto.careerId() : course.getCareer().getId();
        int year = dto.year() != null ? dto.year() : course.getYear();
        int division = dto.division() != null ? dto.division() : course.getDivision();

        if (courseRepository.existsByCareerIdAndYearAndDivisionAndIdNot(careerId, year, division, id)) {
            throw new ResourceAlreadyExistsException(ResourceType.COURSE, "careerId+year+division",
                    careerId + "+" + year + "+" + division);
        }

        if (dto.careerId() != null) {
            Career career = careerService.getById(dto.careerId());
            course.setCareer(career);
        }

        courseMapper.updateFromDTO(course, dto);
        course.setYear(year);
        course.setDivision(division);
        course.setName();

        return courseMapper.toDTO(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = getById(id);
        courseRepository.delete(course);
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByCareer(Long careerId) {
        careerService.getById(careerId);
        return courseRepository.findByCareerId(careerId)
                .stream()
                .map(courseMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        return courseMapper.toDTO(getById(id));
    }

    public Course getById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.COURSE, "id", Long.toString(id)));
    }
}
