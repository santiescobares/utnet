package ar.net.ut.backend.subject;

import ar.net.ut.backend.course.repository.CourseSubjectRepository;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.BackendException;
import ar.net.ut.backend.exception.impl.ResourceAlreadyExistsException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.subject.dto.SubjectCreateDTO;
import ar.net.ut.backend.subject.dto.SubjectDTO;
import ar.net.ut.backend.subject.dto.SubjectUpdateDTO;
import ar.net.ut.backend.subject.event.SubjectCreateEvent;
import ar.net.ut.backend.subject.event.SubjectDeleteEvent;
import ar.net.ut.backend.subject.event.SubjectUpdateEvent;
import ar.net.ut.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final UserService userService;

    private final SubjectRepository subjectRepository;
    private final CourseSubjectRepository courseSubjectRepository;

    private final SubjectMapper subjectMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public SubjectDTO createSubject(SubjectCreateDTO dto) {
        String name = dto.name();
        if (subjectRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException(ResourceType.SUBJECT, "name", name);
        }
        String shortName = dto.shortName();
        if (subjectRepository.existsByShortNameIgnoreCase(shortName)) {
            throw new ResourceAlreadyExistsException(ResourceType.SUBJECT, "shortName", shortName);
        }

        Subject subject = subjectMapper.createEntity(dto);
        subjectRepository.save(subject);

        eventPublisher.publishEvent(new SubjectCreateEvent(userService.getCurrentUser(), subject));

        return subjectMapper.toDTO(subject);
    }

    @Transactional
    public SubjectDTO updateSubject(Long id, SubjectUpdateDTO dto) {
        Subject subject = getById(id);

        String name = dto.name();
        if (name != null && subjectRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ResourceAlreadyExistsException(ResourceType.SUBJECT, "name", name);
        }
        String shortName = dto.shortName();
        if (shortName != null && subjectRepository.existsByShortNameIgnoreCaseAndIdNot(shortName, id)) {
            throw new ResourceAlreadyExistsException(ResourceType.SUBJECT, "shortName", shortName);
        }

        subjectMapper.updateFromDTO(subject, dto);

        eventPublisher.publishEvent(new SubjectUpdateEvent(userService.getCurrentUser(), subject));

        return subjectMapper.toDTO(subject);
    }

    @Transactional
    public void deleteSubject(Long id) {
        Subject subject = getById(id);
        if (courseSubjectRepository.existsBySubjectId(id)) {
            throw new BackendException(
                    "You must remove that subject from all courses in order to delete it",
                    HttpStatus.CONFLICT,
                    "SUBJECT_DELETE_COURSE_CONFLICT"
            );
        }

        subjectRepository.delete(subject);
        subjectRepository.unlinkSubjectFromCorrelatives(id);

        eventPublisher.publishEvent(new SubjectDeleteEvent(userService.getCurrentUser(), subject));
    }

    @Transactional(readOnly = true)
    public List<SubjectDTO> getAllSubjectsDTO() {
        return subjectMapper.toDTOList(subjectRepository.findAll());
    }

    public Subject getById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.SUBJECT, "id", Long.toString(id)));
    }
}
