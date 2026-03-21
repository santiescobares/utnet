package ar.net.ut.backend.course.mapper;

import ar.net.ut.backend.course.CourseReview;
import ar.net.ut.backend.course.dto.review.CourseReviewCreateDTO;
import ar.net.ut.backend.course.dto.review.CourseReviewDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CourseMapper.class}
)
public interface CourseReviewMapper {

    @Mapping(source = "courseId", target = "resource", qualifiedByName = "courseIdToCourse")
    CourseReview createEntity(CourseReviewCreateDTO dto);

    @Mapping(source = "resource.id", target = "courseId")
    @Mapping(source = "postedBy.id", target = "postedById")
    CourseReviewDTO toDTO(CourseReview review);
}
