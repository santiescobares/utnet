package ar.net.ut.backend.course.event.review;

import ar.net.ut.backend.course.CourseReview;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class CourseReviewEvent {

    private final CourseReview courseReview;
}
