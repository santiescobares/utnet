package ar.net.ut.backend.course.mapper;

import ar.net.ut.backend.career.CareerMapper;
import ar.net.ut.backend.course.Course;
import ar.net.ut.backend.course.dto.CourseCreateDTO;
import ar.net.ut.backend.course.dto.CourseDTO;
import ar.net.ut.backend.course.dto.CourseUpdateDTO;
import ar.net.ut.backend.course.repository.CourseRepository;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CareerMapper.class}
)
public abstract class CourseMapper {

    @Autowired
    protected CourseRepository courseRepository;

    @Mapping(target = "career", ignore = true)
    public abstract Course createEntity(CourseCreateDTO dto);

    @Mapping(source = "career.id", target = "careerId")
    public abstract CourseDTO toDTO(Course course);

    @Mapping(source = "career.id", target = "careerId")
    public abstract List<CourseDTO> toDTOList(List<Course> courses);

    @Mapping(source = "careerId", target = "career", qualifiedByName = "careerIdToCareer")
    public abstract void updateFromDTO(@MappingTarget Course course, CourseUpdateDTO dto);

    @Named("courseIdToCourse")
    public Course mapCourseIdToCourse(Long courseId) {
        if (courseId == null) return null;
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.COURSE, "id", courseId.toString()));
    }
}
