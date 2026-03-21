package ar.net.ut.backend.course.event.review;

import ar.net.ut.backend.course.CourseReview;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.Log;
import ar.net.ut.backend.model.event.LoggableEvent;
import lombok.Getter;

@Getter
public abstract class CourseReviewEvent extends LoggableEvent<CourseReview> {

    private final CourseReview courseReview;

    public CourseReviewEvent(CourseReview courseReview, Log.Action action) {
        super(courseReview.getPostedBy(), ResourceType.COURSE_REVIEW, courseReview.getId().toString(), action);
        this.courseReview = courseReview;
    }

    @Override
    public CourseReview getEntity() { return courseReview; }
}
