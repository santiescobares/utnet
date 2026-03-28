package ar.net.ut.backend.course.mapper;

import ar.net.ut.backend.course.CourseSubject;
import ar.net.ut.backend.course.dto.subject.CourseSubjectCreateDTO;
import ar.net.ut.backend.course.dto.subject.CourseSubjectDTO;
import ar.net.ut.backend.course.dto.subject.CourseSubjectUpdateDTO;
import ar.net.ut.backend.subject.SubjectMapper;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
        uses = {CourseMapper.class, SubjectMapper.class}
)
public interface CourseSubjectMapper {

    @Mapping(source = "courseId", target = "course", qualifiedByName = "courseIdToCourse")
    @Mapping(source = "subjectId", target = "subject", qualifiedByName = "subjectIdToSubject")
    CourseSubject createEntity(CourseSubjectCreateDTO dto);

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "subject.id", target = "subjectId")
    CourseSubjectDTO toDTO(CourseSubject courseSubject);

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "subject.id", target = "subjectId")
    List<CourseSubjectDTO> toDTOList(List<CourseSubject> courseSubjects);

    void updateFromDTO(@MappingTarget CourseSubject courseSubject, CourseSubjectUpdateDTO dto);
}
