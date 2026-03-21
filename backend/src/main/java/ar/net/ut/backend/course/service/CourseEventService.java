package ar.net.ut.backend.course.service;

import ar.net.ut.backend.course.CourseEvent;
import ar.net.ut.backend.course.mapper.CourseEventMapper;
import ar.net.ut.backend.course.repository.CourseEventRepository;
import ar.net.ut.backend.course.dto.event.CourseEventCreateDTO;
import ar.net.ut.backend.course.dto.event.CourseEventDTO;
import ar.net.ut.backend.course.dto.event.CourseEventUpdateDTO;
import ar.net.ut.backend.course.event.event.CourseEventResourceCreateEvent;
import ar.net.ut.backend.course.event.event.CourseEventResourceDeleteEvent;
import ar.net.ut.backend.course.event.event.CourseEventResourceUpdateEvent;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.service.UserService;
import ar.net.ut.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static ar.net.ut.backend.Global.RedisKeys.*;

@Service
@RequiredArgsConstructor
public class CourseEventService {

    private static final long CONTRIBUTION_COOLDOWN = 3600; // In seconds

    private final UserService userService;
    private final CourseService courseService;

    private final CourseEventRepository courseEventRepository;

    private final CourseEventMapper courseEventMapper;

    private final StringRedisTemplate redisTemplate;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CourseEventDTO createCourseEvent(CourseEventCreateDTO dto) {
        User user = userService.getCurrentUser();
        CourseEvent courseEvent = courseEventMapper.createEntity(dto);
        courseEvent.setCourse(courseService.getById(dto.courseId()));
        courseEvent.setCreatedBy(user);

        courseEventRepository.save(courseEvent);

        setOnContributionCooldown(user.getId());

        eventPublisher.publishEvent(new CourseEventResourceCreateEvent(courseEvent));

        return courseEventMapper.toDTO(courseEvent);
    }

    @Transactional
    public CourseEventDTO updateCourseEvent(Long id, CourseEventUpdateDTO dto) {
        User user = userService.getCurrentUser();
        CourseEvent courseEvent = getById(id);

        courseEventMapper.updateFromDTO(courseEvent, dto);
        courseEvent.setLastEditor(user);

        setOnContributionCooldown(user.getId());

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
        return courseEventMapper.toDTOList(courseEventRepository.findByCourseId(courseId));
    }

    @Transactional(readOnly = true)
    public List<CourseEventDTO> getEventsByCourseAndDateRange(Long courseId, LocalDate from, LocalDate to) {
        courseService.getById(courseId);
        return courseEventMapper.toDTOList(courseEventRepository.findByCourseIdAndDateBetween(courseId, from, to));
    }

    public CourseEvent getById(Long id) {
        return courseEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.COURSE_EVENT, "id", Long.toString(id)));
    }

    private void setOnContributionCooldown(UUID userId) {
        redisTemplate.opsForValue().set(
                COURSE_EVENT_COOLDOWN + userId.toString(),
                Instant.now().toString(),
                CONTRIBUTION_COOLDOWN,
                TimeUnit.SECONDS
        );
    }

    public boolean isOnContributionCooldown(UUID userId) {
        return redisTemplate.hasKey(COURSE_EVENT_COOLDOWN + userId.toString());
    }
}
