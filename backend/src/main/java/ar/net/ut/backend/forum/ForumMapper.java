package ar.net.ut.backend.forum;

import ar.net.ut.backend.forum.dto.ForumDTO;
import ar.net.ut.backend.forum.dto.ForumUpdateDTO;
import ar.net.ut.backend.forum.entity.Forum;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ForumMapper {

    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "createdById", source = "createdBy.id")
    ForumDTO toDTO(Forum forum);

    @Mapping(target = "topic", ignore = true)
    void updateFromDTO(@MappingTarget Forum forum, ForumUpdateDTO dto);
}
