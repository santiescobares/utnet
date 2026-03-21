package ar.net.ut.backend.forum.mapper;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.forum.dto.topic.ForumTopicCreateDTO;
import ar.net.ut.backend.forum.dto.topic.ForumTopicDTO;
import ar.net.ut.backend.forum.dto.topic.ForumTopicUpdateDTO;
import ar.net.ut.backend.forum.ForumTopic;
import ar.net.ut.backend.forum.repository.ForumTopicRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ForumTopicMapper {

    @Autowired
    protected ForumTopicRepository forumTopicRepository;

    public abstract ForumTopic createEntity(ForumTopicCreateDTO dto);

    public abstract ForumTopicDTO toDTO(ForumTopic forumTopic);

    public abstract List<ForumTopicDTO> toDTOList(List<ForumTopic> forumTopics);

    public abstract void updateFromDTO(@MappingTarget ForumTopic forumTopic, ForumTopicUpdateDTO dto);

    @Named("topicIdToTopic")
    public ForumTopic mapTopicIdToTopic(Long topicId) {
        if (topicId == null) return null;
        return forumTopicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.FORUM_TOPIC, "id", topicId.toString()));
    }
}
