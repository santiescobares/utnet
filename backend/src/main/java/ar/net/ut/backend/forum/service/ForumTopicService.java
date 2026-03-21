package ar.net.ut.backend.forum.service;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceAlreadyExistsException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.forum.repository.ForumTopicRepository;
import ar.net.ut.backend.forum.dto.topic.ForumTopicCreateDTO;
import ar.net.ut.backend.forum.dto.topic.ForumTopicDTO;
import ar.net.ut.backend.forum.dto.topic.ForumTopicUpdateDTO;
import ar.net.ut.backend.forum.ForumTopic;
import ar.net.ut.backend.forum.event.topic.ForumTopicCreateEvent;
import ar.net.ut.backend.forum.event.topic.ForumTopicDeleteEvent;
import ar.net.ut.backend.forum.event.topic.ForumTopicUpdateEvent;
import ar.net.ut.backend.forum.mapper.ForumTopicMapper;
import ar.net.ut.backend.user.service.UserService;
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

    private final UserService userService;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ForumTopicDTO createForumTopic(ForumTopicCreateDTO dto) {
        String name = dto.name();
        if (forumTopicRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException(ResourceType.FORUM_TOPIC, "name", name);
        }

        ForumTopic forumTopic = forumTopicMapper.createEntity(dto);
        forumTopicRepository.save(forumTopic);

        eventPublisher.publishEvent(new ForumTopicCreateEvent(userService.getCurrentUser(), forumTopic));

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

        eventPublisher.publishEvent(new ForumTopicUpdateEvent(userService.getCurrentUser(), forumTopic));

        return forumTopicMapper.toDTO(forumTopic);
    }

    @Transactional
    public void deleteForumTopic(Long id) {
        ForumTopic forumTopic = getById(id);
        forumTopicRepository.delete(forumTopic);

        eventPublisher.publishEvent(new ForumTopicDeleteEvent(userService.getCurrentUser(), forumTopic));
    }

    @Transactional(readOnly = true)
    public List<ForumTopicDTO> getAllForumTopics() {
        return forumTopicMapper.toDTOList(forumTopicRepository.findAllByOrderBySortPositionDesc());
    }

    public ForumTopic getById(Long id) {
        return forumTopicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.FORUM_TOPIC, "id", Long.toString(id)));
    }
}
