package ar.net.ut.backend.course.service;

import ar.net.ut.backend.context.RequestContextData;
import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.course.event.review.CourseReviewAddInteractionEvent;
import ar.net.ut.backend.course.event.review.CourseReviewRemoveInteractionEvent;
import ar.net.ut.backend.course.mapper.CourseReviewMapper;
import ar.net.ut.backend.course.repository.CourseRepository;
import ar.net.ut.backend.course.repository.CourseReviewRepository;
import ar.net.ut.backend.course.dto.review.CourseReviewCreateDTO;
import ar.net.ut.backend.course.dto.review.CourseReviewDTO;
import ar.net.ut.backend.course.CourseReview;
import ar.net.ut.backend.course.event.review.CourseReviewCreateEvent;
import ar.net.ut.backend.course.event.review.CourseReviewDeleteEvent;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.UserComment;
import ar.net.ut.backend.user.UserInteraction;
import ar.net.ut.backend.user.enums.Role;
import ar.net.ut.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseReviewService {

    private final CourseService courseService;
    private final UserService userService;

    private final CourseReviewRepository courseReviewRepository;

    private final CourseReviewMapper courseReviewMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CourseReviewDTO createReview(CourseReviewCreateDTO dto) {
        Long courseId = dto.courseId();

        if (courseReviewRepository.existsByResourceIdAndPostedById(courseId, RequestContextHolder.getCurrentSession().userId())) {
            throw new InvalidOperationException("User has already submitted a review for course with id = " + courseId);
        }

        CourseReview review = courseReviewMapper.createEntity(dto);
        review.setPostedBy(userService.getCurrentUser());

        courseReviewRepository.save(review);

        eventPublisher.publishEvent(new CourseReviewCreateEvent(review));

        return courseReviewMapper.toDTO(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        RequestContextData session = RequestContextHolder.getCurrentSession();
        CourseReview review = getById(id);

        boolean isAuthor = review.getPostedBy().getId().equals(session.userId());
        boolean isAdmin = session.role() == Role.ADMINISTRATOR;

        if (!isAuthor && !isAdmin) {
            throw new InvalidOperationException("You can't delete that comment");
        }

        courseReviewRepository.delete(review);

        eventPublisher.publishEvent(new CourseReviewDeleteEvent(review));
    }

    @Transactional
    public void addReviewInteraction(Long id, UserInteraction.Type type) {
        if (type != UserInteraction.Type.LIKE && type != UserInteraction.Type.DISLIKE) {
            throw new IllegalArgumentException("Invalid interaction type for review");
        }

        CourseReview courseReview = getById(id);

        if (type == UserInteraction.Type.LIKE) {
            courseReview.addLike();
        } else {
            courseReview.addDislike();
        }

        eventPublisher.publishEvent(new CourseReviewAddInteractionEvent(courseReview, type));
    }

    @Transactional
    public void removeReviewInteraction(Long id, UserInteraction.Type type) {
        if (type != UserInteraction.Type.LIKE && type != UserInteraction.Type.DISLIKE) {
            throw new IllegalArgumentException("Invalid interaction type for review");
        }

        CourseReview courseReview = getById(id);

        if (type == UserInteraction.Type.LIKE) {
            courseReview.removeLike();
        } else {
            courseReview.removeDislike();
        }

        eventPublisher.publishEvent(new CourseReviewRemoveInteractionEvent(courseReview, type));
    }

    @Transactional(readOnly = true)
    public Page<CourseReviewDTO> getReviewsByCourse(Long courseId, Pageable pageable) {
        courseService.getById(courseId);
        return courseReviewRepository.findByResourceId(courseId, pageable).map(courseReviewMapper::toDTO);
    }

    public CourseReview getById(Long id) {
        return courseReviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.COURSE_REVIEW, "id", Long.toString(id)));
    }
}
