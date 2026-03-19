package ar.net.ut.backend.course.event;

import ar.net.ut.backend.course.entity.CourseReview;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CourseReviewEvent {

    private final CourseReview courseReview;
}
