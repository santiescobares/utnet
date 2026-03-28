package ar.net.ut.backend.course.mapper;

import ar.net.ut.backend.course.CourseEvent;
import ar.net.ut.backend.course.dto.event.CourseEventCreateDTO;
import ar.net.ut.backend.course.dto.event.CourseEventDTO;
import ar.net.ut.backend.course.dto.event.CourseEventUpdateDTO;
import ar.net.ut.backend.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CourseMapper.class, UserMapper.class}
)
public interface CourseEventMapper {

    @Mapping(source = "courseId", target = "course", qualifiedByName = "courseIdToCourse")
    CourseEvent createEntity(CourseEventCreateDTO dto);

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "tag.color", target = "tagColor")
    CourseEventDTO toDTO(CourseEvent courseEvent);

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "tag.color", target = "tagColor")
    List<CourseEventDTO> toDTOList(List<CourseEvent> courseEvents);

    void updateFromDTO(@MappingTarget CourseEvent courseEvent, CourseEventUpdateDTO dto);
}
