package ar.net.ut.backend.course.listener;

import ar.net.ut.backend.course.repository.CourseSubjectRepository;
import ar.net.ut.backend.exception.BackendException;
import ar.net.ut.backend.subject.event.SubjectDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseSubjectListener {

    private final CourseSubjectRepository courseSubjectRepository;

    @EventListener
    public void onSubjectDelete(SubjectDeleteEvent event) {
        if (courseSubjectRepository.existsBySubjectId(event.getSubject().getId())) {
            throw new BackendException(
                    "You must remove that subject from all courses in order to delete it",
                    HttpStatus.CONFLICT,
                    "SUBJECT_DELETE_COURSE_CONFLICT"
            );
        }
    }
}