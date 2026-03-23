package ar.net.ut.backend.course.service;

import ar.net.ut.backend.career.Career;
import ar.net.ut.backend.career.CareerService;
import ar.net.ut.backend.course.mapper.CourseMapper;
import ar.net.ut.backend.course.repository.CourseRepository;
import ar.net.ut.backend.course.dto.CourseCreateDTO;
import ar.net.ut.backend.course.dto.CourseDTO;
import ar.net.ut.backend.course.dto.CourseUpdateDTO;
import ar.net.ut.backend.course.Course;
import ar.net.ut.backend.course.event.CourseCreateEvent;
import ar.net.ut.backend.course.event.CourseDeleteEvent;
import ar.net.ut.backend.course.event.CourseUpdateEvent;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceAlreadyExistsException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.repository.UserRepository;
import ar.net.ut.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CareerService careerService;
    private final UserService userService;

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    private final CourseMapper courseMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CourseDTO createCourse(CourseCreateDTO dto) {
        Long careerId = dto.careerId();
        Career career = careerService.getById(careerId);

        int year = dto.year();
        int division = dto.division();

        if (courseRepository.existsByCareerIdAndYearAndDivision(careerId, year, division)) {
            throw new ResourceAlreadyExistsException(ResourceType.COURSE, "careerId + year + division",
                    careerId + " + " + year + " + " + division);
        }

        Course course = courseMapper.createEntity(dto);
        course.setCareer(career);
        course.setName();

        courseRepository.save(course);

        eventPublisher.publishEvent(new CourseCreateEvent(userService.getCurrentUser(), course));

        return courseMapper.toDTO(course);
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseUpdateDTO dto) {
        Course course = getById(id);

        Long careerId = dto.careerId() != null ? dto.careerId() : course.getCareer().getId();
        int year = dto.year() != null ? dto.year() : course.getYear();
        int division = dto.division() != null ? dto.division() : course.getDivision();

        if (courseRepository.existsByCareerIdAndYearAndDivisionAndIdNot(careerId, year, division, id)) {
            throw new ResourceAlreadyExistsException(ResourceType.COURSE, "careerId + year + division",
                    careerId + " + " + year + " + " + division);
        }

        courseMapper.updateFromDTO(course, dto);
        course.setName();

        eventPublisher.publishEvent(new CourseUpdateEvent(userService.getCurrentUser(), course));

        return courseMapper.toDTO(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        Course course = getById(id);

        userRepository.unlinkUsersFromCourse(id);
        courseRepository.delete(course);

        eventPublisher.publishEvent(new CourseDeleteEvent(userService.getCurrentUser(), course));
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByCareer(Long careerId) {
        careerService.getById(careerId);
        return courseMapper.toDTOList(courseRepository.findAllByCareerId(careerId));
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        return courseMapper.toDTO(getById(id));
    }

    public Course getById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.COURSE, "id", id.toString()));
    }
}
