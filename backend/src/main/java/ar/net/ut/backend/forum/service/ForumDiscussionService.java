package ar.net.ut.backend.forum.service;

import ar.net.ut.backend.context.RequestContextData;
import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.forum.ForumDiscussion;
import ar.net.ut.backend.forum.dto.ForumDiscussionDTO;
import ar.net.ut.backend.forum.mapper.ForumDiscussionMapper;
import ar.net.ut.backend.forum.repository.ForumDiscussionRepository;
import ar.net.ut.backend.forum.dto.ForumDiscussionCreateDTO;
import ar.net.ut.backend.forum.dto.ForumDiscussionUpdateDTO;
import ar.net.ut.backend.forum.ForumTopic;
import ar.net.ut.backend.forum.event.ForumDiscussionCreateDiscussionEvent;
import ar.net.ut.backend.forum.event.ForumDiscussionDeleteEvent;
import ar.net.ut.backend.forum.event.ForumDiscussionUpdateEvent;
import ar.net.ut.backend.user.enums.Role;
import ar.net.ut.backend.user.service.UserService;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.util.RandomUtil;
import ar.net.ut.backend.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForumDiscussionService {

    private static final int MAX_FORUM_DISCUSSIONS = 3;

    private final ForumTopicService forumTopicService;
    private final UserService userService;

    private final ForumDiscussionRepository forumDiscussionRepository;

    private final ForumDiscussionMapper forumDiscussionMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ForumDiscussionDTO createDiscussion(ForumDiscussionCreateDTO dto) {
        RequestContextData session = RequestContextHolder.getCurrentSession();
        if (forumDiscussionRepository.countAllByCreatedById(session.userId()) >= MAX_FORUM_DISCUSSIONS
                && session.role() != Role.ADMINISTRATOR) {
            throw new InvalidOperationException("You can't have more than " + MAX_FORUM_DISCUSSIONS
                    + " forum discussions open simultaneously");
        }

        ForumTopic topic = forumTopicService.getById(dto.topicId());
        User user = userService.getCurrentUser();

        ForumDiscussion forumDiscussion = new ForumDiscussion();
        forumDiscussion.setTopic(topic);
        forumDiscussion.setCreatedBy(user);
        forumDiscussion.setTitle(dto.title());
        forumDiscussion.setSlug(generateUniqueSlug(dto.title()));

        if (user.getRole() == Role.ADMINISTRATOR) {
            forumDiscussion.setOpen(dto.open());
            forumDiscussion.setPermanent(dto.permanent());
        } else {
            forumDiscussion.setOpen(true);
        }

        forumDiscussionRepository.save(forumDiscussion);

        eventPublisher.publishEvent(new ForumDiscussionCreateDiscussionEvent(forumDiscussion));

        return forumDiscussionMapper.toDTO(forumDiscussion);
    }

    @Transactional
    public ForumDiscussionDTO updateDiscussion(Long id, ForumDiscussionUpdateDTO dto) {
        ForumDiscussion forumDiscussion = getById(id);
        canManage(forumDiscussion);

        forumDiscussionMapper.updateFromDTO(forumDiscussion, dto);

        if (dto.title() != null) {
            String slug = generateUniqueSlug(dto.title());
            forumDiscussion.setSlug(slug);
        }

        eventPublisher.publishEvent(new ForumDiscussionUpdateEvent(forumDiscussion));

        return forumDiscussionMapper.toDTO(forumDiscussion);
    }

    @Transactional
    public void deleteDiscussion(Long id) {
        ForumDiscussion forumDiscussion = getById(id);
        canManage(forumDiscussion);

        forumDiscussionRepository.delete(forumDiscussion);

        eventPublisher.publishEvent(new ForumDiscussionDeleteEvent(forumDiscussion));
    }

    @Transactional
    public ForumDiscussionDTO openDiscussion(Long id) {
        ForumDiscussion forumDiscussion = getById(id);
        canManage(forumDiscussion);

        forumDiscussion.setOpen(true);

        eventPublisher.publishEvent(new ForumDiscussionUpdateEvent(forumDiscussion));

        return forumDiscussionMapper.toDTO(forumDiscussion);
    }

    @Transactional
    public ForumDiscussionDTO closeDiscussion(Long id) {
        ForumDiscussion forumDiscussion = getById(id);
        canManage(forumDiscussion);

        forumDiscussion.setOpen(false);

        eventPublisher.publishEvent(new ForumDiscussionUpdateEvent(forumDiscussion));

        return forumDiscussionMapper.toDTO(forumDiscussion);
    }

    @Transactional(readOnly = true)
    public ForumDiscussionDTO getDiscussionById(Long id) {
        return forumDiscussionMapper.toDTO(getById(id));
    }

    @Transactional(readOnly = true)
    public ForumDiscussionDTO getDiscussionBySlug(String slug) {
        return forumDiscussionRepository.findBySlug(slug)
                .map(forumDiscussionMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.FORUM_DISCUSSION, "slug", slug));
    }

    @Transactional(readOnly = true)
    public Page<ForumDiscussionDTO> getAllDiscussions(Pageable pageable) {
        return forumDiscussionRepository.findAllByOrderBySortPositionAsc(pageable).map(forumDiscussionMapper::toDTO);
    }

    public ForumDiscussion getById(Long id) {
        return forumDiscussionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.FORUM_DISCUSSION, "id", Long.toString(id)));
    }

    private String generateUniqueSlug(String title) {
        String baseSlug = StringUtil.normalize(title);
        if (!forumDiscussionRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }
        return baseSlug + "-" + RandomUtil.randomHexString().substring(0, 6);
    }

    public void canManage(ForumDiscussion forumDiscussion) {
        RequestContextData session = RequestContextHolder.getCurrentSession();
        if (session.role() != Role.ADMINISTRATOR && !forumDiscussion.getCreatedBy().getId().equals(session.userId())) {
            throw new InvalidOperationException("You don't have permission to manage that forum discussion");
        }
    }
}
