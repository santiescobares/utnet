package ar.net.ut.backend.forum.mapper;

import ar.net.ut.backend.forum.dto.ForumDiscussionDTO;
import ar.net.ut.backend.forum.dto.ForumDiscussionUpdateDTO;
import ar.net.ut.backend.forum.ForumDiscussion;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ForumTopicMapper.class}
)
public interface ForumDiscussionMapper {

    @Mapping(target = "topicId", source = "topic.id")
    @Mapping(target = "createdById", source = "createdBy.id")
    ForumDiscussionDTO toDTO(ForumDiscussion forumDiscussion);

    @Mapping(source = "topicId", target = "topic", qualifiedByName = "topicIdToTopic")
    void updateFromDTO(@MappingTarget ForumDiscussion forumDiscussion, ForumDiscussionUpdateDTO dto);
}
