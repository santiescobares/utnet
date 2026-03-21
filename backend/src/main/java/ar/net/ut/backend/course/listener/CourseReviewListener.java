package ar.net.ut.backend.course.listener;

import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.course.event.review.CourseReviewAddInteractionEvent;
import ar.net.ut.backend.course.event.review.CourseReviewRemoveInteractionEvent;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.service.UserInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseReviewListener {

    private final UserInteractionService userInteractionService;

    @EventListener
    public void onCourseReviewAddInteraction(CourseReviewAddInteractionEvent event) {
        userInteractionService.createInteraction(
                RequestContextHolder.getCurrentSession().userId(),
                event.getInteractionType(),
                ResourceType.COURSE_REVIEW,
                event.getEntity().getId().toString()
        );
    }

    @EventListener
    public void onCourseReviewRemoveInteraction(CourseReviewRemoveInteractionEvent event) {
        userInteractionService.deleteInteraction(
                RequestContextHolder.getCurrentSession().userId(),
                event.getInteractionType(),
                ResourceType.COURSE_REVIEW,
                event.getEntity().getId().toString()
        );
    }
}
