package ar.net.ut.backend.course.service;

import ar.net.ut.backend.course.mapper.CourseEventMapper;
import ar.net.ut.backend.course.repository.CourseEventRepository;
import ar.net.ut.backend.course.dto.event.CourseEventCreateDTO;
import ar.net.ut.backend.course.dto.event.CourseEventDTO;
import ar.net.ut.backend.course.dto.event.CourseEventUpdateDTO;
import ar.net.ut.backend.course.CourseEvent;
import ar.net.ut.backend.course.event.CourseEventResourceCreateEvent;
import ar.net.ut.backend.course.event.CourseEventResourceDeleteEvent;
import ar.net.ut.backend.course.event.CourseEventResourceUpdateEvent;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.UserService;
import ar.net.ut.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseEventService {

    private final CourseEventRepository courseEventRepository;

    private final CourseEventMapper courseEventMapper;

    private final CourseService courseService;

    private final UserService userService;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CourseEventDTO createCourseEvent(CourseEventCreateDTO dto) {
        User currentUser = userService.getCurrentUser();

        CourseEvent courseEvent = courseEventMapper.createEntity(dto);
        courseEvent.setCourse(courseService.getById(dto.courseId()));
        courseEvent.setCreatedBy(currentUser);

        courseEventRepository.save(courseEvent);

        eventPublisher.publishEvent(new CourseEventResourceCreateEvent(courseEvent));

        return courseEventMapper.toDTO(courseEvent);
    }

    @Transactional
    public CourseEventDTO updateCourseEvent(Long id, CourseEventUpdateDTO dto) {
        CourseEvent courseEvent = getById(id);
        User currentUser = userService.getCurrentUser();

        courseEventMapper.updateFromDTO(courseEvent, dto);
        courseEvent.setLastEditor(currentUser);

        // TODO: register UserContribution of 1 point for calendar edit (info.txt)

        eventPublisher.publishEvent(new CourseEventResourceUpdateEvent(courseEvent));

        return courseEventMapper.toDTO(courseEvent);
    }

    @Transactional
    public void deleteCourseEvent(Long id) {
        CourseEvent courseEvent = getById(id);
        courseEventRepository.delete(courseEvent);

        eventPublisher.publishEvent(new CourseEventResourceDeleteEvent(courseEvent));
    }

    @Transactional(readOnly = true)
    public List<CourseEventDTO> getEventsByCourse(Long courseId) {
        courseService.getById(courseId);
        return courseEventRepository.findByCourseId(courseId)
                .stream()
                .map(courseEventMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseEventDTO> getEventsByCourseAndDateRange(Long courseId, LocalDate from, LocalDate to) {
        courseService.getById(courseId);
        return courseEventRepository.findByCourseIdAndDateBetween(courseId, from, to)
                .stream()
                .map(courseEventMapper::toDTO)
                .toList();
    }

    public CourseEvent getById(Long id) {
        return courseEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.COURSE_EVENT, "id", Long.toString(id)));
    }
}
