package ar.net.ut.backend.course;

import ar.net.ut.backend.course.dto.CourseReviewCreateDTO;
import ar.net.ut.backend.course.dto.CourseReviewDTO;
import ar.net.ut.backend.course.entity.CourseReview;
import ar.net.ut.backend.course.event.CourseReviewCreateEvent;
import ar.net.ut.backend.course.event.CourseReviewDeleteEvent;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.UserService;
import ar.net.ut.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseReviewService {

    private final CourseReviewRepository courseReviewRepository;

    private final CourseReviewMapper courseReviewMapper;

    private final CourseService courseService;

    private final UserService userService;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CourseReviewDTO createReview(CourseReviewCreateDTO dto) {
        User currentUser = userService.getCurrentUser();
        Long courseId = dto.courseId();

        if (courseReviewRepository.existsByResourceIdAndPostedById(courseId, currentUser.getId())) {
            throw new InvalidOperationException("User has already submitted a review for course with id=" + courseId);
        }

        CourseReview review = courseReviewMapper.createEntity(dto);
        review.setResource(courseService.getById(courseId));
        review.setPostedBy(currentUser);

        courseReviewRepository.save(review);

        eventPublisher.publishEvent(new CourseReviewCreateEvent(review));

        return courseReviewMapper.toDTO(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        CourseReview review = getById(id);
        // TODO: authorize — only the author or CONTRIBUTOR_3+ should be able to delete
        courseReviewRepository.delete(review);

        eventPublisher.publishEvent(new CourseReviewDeleteEvent(review));
    }

    @Transactional(readOnly = true)
    public List<CourseReviewDTO> getReviewsByCourse(Long courseId) {
        courseService.getById(courseId);
        return courseReviewRepository.findByResourceId(courseId)
                .stream()
                .map(courseReviewMapper::toDTO)
                .toList();
    }

    public CourseReview getById(Long id) {
        return courseReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.COURSE_REVIEW, "id", Long.toString(id)));
    }
}
