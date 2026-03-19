package ar.net.ut.backend.forum;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceAlreadyExistsException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.forum.dto.ForumTopicCreateDTO;
import ar.net.ut.backend.forum.dto.ForumTopicDTO;
import ar.net.ut.backend.forum.dto.ForumTopicUpdateDTO;
import ar.net.ut.backend.forum.entity.ForumTopic;
import ar.net.ut.backend.forum.event.ForumTopicCreateEvent;
import ar.net.ut.backend.forum.event.ForumTopicDeleteEvent;
import ar.net.ut.backend.forum.event.ForumTopicUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumTopicService {

    private final ForumTopicRepository forumTopicRepository;
    private final ForumTopicMapper forumTopicMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ForumTopicDTO createForumTopic(ForumTopicCreateDTO dto) {
        String name = dto.name();
        if (forumTopicRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException(ResourceType.FORUM_TOPIC, "name", name);
        }

        ForumTopic forumTopic = forumTopicMapper.createEntity(dto);
        forumTopicRepository.save(forumTopic);

        eventPublisher.publishEvent(new ForumTopicCreateEvent(forumTopic));

        return forumTopicMapper.toDTO(forumTopic);
    }

    @Transactional
    public ForumTopicDTO updateForumTopic(Long id, ForumTopicUpdateDTO dto) {
        ForumTopic forumTopic = getById(id);

        String name = dto.name();
        if (name != null && forumTopicRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException(ResourceType.FORUM_TOPIC, "name", name);
        }

        forumTopicMapper.updateFromDTO(forumTopic, dto);

        eventPublisher.publishEvent(new ForumTopicUpdateEvent(forumTopic));

        return forumTopicMapper.toDTO(forumTopic);
    }

    @Transactional
    public void deleteForumTopic(Long id) {
        ForumTopic forumTopic = getById(id);
        forumTopicRepository.delete(forumTopic);

        eventPublisher.publishEvent(new ForumTopicDeleteEvent(forumTopic));
    }

    @Transactional(readOnly = true)
    public List<ForumTopicDTO> getAllForumTopics() {
        return forumTopicRepository.findAllByOrderBySortPositionAsc()
                .stream()
                .map(forumTopicMapper::toDTO)
                .toList();
    }

    public ForumTopic getById(Long id) {
        return forumTopicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.FORUM_TOPIC, "id", Long.toString(id)));
    }
}
