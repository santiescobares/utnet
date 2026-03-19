package ar.net.ut.backend.course.mapper;

import ar.net.ut.backend.course.CourseSubject;
import ar.net.ut.backend.course.dto.subject.CourseSubjectCreateDTO;
import ar.net.ut.backend.course.dto.subject.CourseSubjectDTO;
import ar.net.ut.backend.course.dto.subject.CourseSubjectUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseSubjectMapper {

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "subject", ignore = true)
    CourseSubject createEntity(CourseSubjectCreateDTO dto);

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "subject.id", target = "subjectId")
    CourseSubjectDTO toDTO(CourseSubject courseSubject);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "subject", ignore = true)
    void updateFromDTO(@MappingTarget CourseSubject courseSubject, CourseSubjectUpdateDTO dto);
}
