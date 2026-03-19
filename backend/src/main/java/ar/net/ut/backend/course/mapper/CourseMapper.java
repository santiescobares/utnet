package ar.net.ut.backend.course.mapper;

import ar.net.ut.backend.course.Course;
import ar.net.ut.backend.course.dto.CourseCreateDTO;
import ar.net.ut.backend.course.dto.CourseDTO;
import ar.net.ut.backend.course.dto.CourseUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseMapper {

    @Mapping(target = "career", ignore = true)
    Course createEntity(CourseCreateDTO dto);

    @Mapping(source = "career.id", target = "careerId")
    CourseDTO toDTO(Course course);

    @Mapping(target = "career", ignore = true)
    @Mapping(target = "name", ignore = true)
    void updateFromDTO(@MappingTarget Course course, CourseUpdateDTO dto);
}
