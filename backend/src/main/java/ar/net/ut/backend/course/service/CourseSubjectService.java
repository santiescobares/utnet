package ar.net.ut.backend.course.service;

import ar.net.ut.backend.course.mapper.CourseSubjectMapper;
import ar.net.ut.backend.course.repository.CourseSubjectRepository;
import ar.net.ut.backend.course.dto.subject.CourseSubjectCreateDTO;
import ar.net.ut.backend.course.dto.subject.CourseSubjectDTO;
import ar.net.ut.backend.course.dto.subject.CourseSubjectUpdateDTO;
import ar.net.ut.backend.course.CourseSubject;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceAlreadyExistsException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.subject.Subject;
import ar.net.ut.backend.subject.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseSubjectService {

    private final CourseSubjectRepository courseSubjectRepository;

    private final CourseSubjectMapper courseSubjectMapper;

    private final CourseService courseService;

    private final SubjectService subjectService;

    @Transactional
    public CourseSubjectDTO addSubjectToCourse(CourseSubjectCreateDTO dto) {
        Long courseId = dto.courseId();
        Long subjectId = dto.subjectId();

        if (courseSubjectRepository.existsByCourseIdAndSubjectId(courseId, subjectId)) {
            throw new ResourceAlreadyExistsException(ResourceType.COURSE, "courseId+subjectId",
                    courseId + "+" + subjectId);
        }

        Subject subject = subjectService.getById(subjectId);
        CourseSubject courseSubject = courseSubjectMapper.createEntity(dto);
        courseSubject.setCourse(courseService.getById(courseId));
        courseSubject.setSubject(subject);

        courseSubjectRepository.save(courseSubject);

        return courseSubjectMapper.toDTO(courseSubject);
    }

    @Transactional
    public CourseSubjectDTO updateCourseSubject(Long id, CourseSubjectUpdateDTO dto) {
        CourseSubject courseSubject = getById(id);
        courseSubjectMapper.updateFromDTO(courseSubject, dto);
        return courseSubjectMapper.toDTO(courseSubject);
    }

    @Transactional
    public void removeCourseSubject(Long id) {
        CourseSubject courseSubject = getById(id);
        courseSubjectRepository.delete(courseSubject);
    }

    @Transactional(readOnly = true)
    public List<CourseSubjectDTO> getSubjectsByCourse(Long courseId) {
        courseService.getById(courseId);
        return courseSubjectRepository.findByCourseId(courseId)
                .stream()
                .map(courseSubjectMapper::toDTO)
                .toList();
    }

    public CourseSubject getById(Long id) {
        return courseSubjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.COURSE, "courseSubjectId", Long.toString(id)));
    }
}
