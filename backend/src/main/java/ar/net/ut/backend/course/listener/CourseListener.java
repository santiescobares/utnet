package ar.net.ut.backend.course.listener;

import ar.net.ut.backend.career.event.CareerDeleteEvent;
import ar.net.ut.backend.course.repository.CourseRepository;
import ar.net.ut.backend.exception.BackendException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseListener {

    private final CourseRepository courseRepository;

    @EventListener
    public void onCareerDelete(CareerDeleteEvent event) {
        if (courseRepository.existsByCareerId(event.getCareer().getId())) {
            throw new BackendException(
                    "You must remove all courses within that career in order to delete it",
                    HttpStatus.CONFLICT,
                    "CAREER_DELETE_COURSE_CONFLICT"
            );
        }
    }
}
