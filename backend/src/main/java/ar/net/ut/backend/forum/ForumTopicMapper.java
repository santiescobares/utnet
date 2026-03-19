package ar.net.ut.backend.forum;

import ar.net.ut.backend.forum.dto.ForumTopicCreateDTO;
import ar.net.ut.backend.forum.dto.ForumTopicDTO;
import ar.net.ut.backend.forum.dto.ForumTopicUpdateDTO;
import ar.net.ut.backend.forum.entity.ForumTopic;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ForumTopicMapper {

    ForumTopic createEntity(ForumTopicCreateDTO dto);

    ForumTopicDTO toDTO(ForumTopic forumTopic);

    void updateFromDTO(@MappingTarget ForumTopic forumTopic, ForumTopicUpdateDTO dto);
}
