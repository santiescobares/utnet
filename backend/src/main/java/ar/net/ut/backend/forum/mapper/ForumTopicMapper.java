package ar.net.ut.backend.forum.mapper;

import ar.net.ut.backend.forum.dto.topic.ForumTopicCreateDTO;
import ar.net.ut.backend.forum.dto.topic.ForumTopicDTO;
import ar.net.ut.backend.forum.dto.topic.ForumTopicUpdateDTO;
import ar.net.ut.backend.forum.ForumTopic;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ForumTopicMapper {

    ForumTopic createEntity(ForumTopicCreateDTO dto);

    ForumTopicDTO toDTO(ForumTopic forumTopic);

    void updateFromDTO(@MappingTarget ForumTopic forumTopic, ForumTopicUpdateDTO dto);
}
