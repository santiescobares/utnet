package ar.net.ut.backend.course.mapper;

import ar.net.ut.backend.course.CourseEvent;
import ar.net.ut.backend.course.dto.event.CourseEventCreateDTO;
import ar.net.ut.backend.course.dto.event.CourseEventDTO;
import ar.net.ut.backend.course.dto.event.CourseEventUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseEventMapper {

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastEditor", ignore = true)
    CourseEvent createEntity(CourseEventCreateDTO dto);

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "lastEditor.id", target = "lastEditorId")
    CourseEventDTO toDTO(CourseEvent courseEvent);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastEditor", ignore = true)
    void updateFromDTO(@MappingTarget CourseEvent courseEvent, CourseEventUpdateDTO dto);
}
