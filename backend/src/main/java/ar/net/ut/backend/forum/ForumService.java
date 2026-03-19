package ar.net.ut.backend.forum;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.forum.dto.ForumCreateDTO;
import ar.net.ut.backend.forum.dto.ForumDTO;
import ar.net.ut.backend.forum.dto.ForumUpdateDTO;
import ar.net.ut.backend.forum.entity.Forum;
import ar.net.ut.backend.forum.entity.ForumTopic;
import ar.net.ut.backend.forum.event.ForumCreateEvent;
import ar.net.ut.backend.forum.event.ForumDeleteEvent;
import ar.net.ut.backend.forum.event.ForumUpdateEvent;
import ar.net.ut.backend.user.UserService;
import ar.net.ut.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ForumService {

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");

    private final ForumRepository forumRepository;
    private final ForumTopicService forumTopicService;
    private final UserService userService;
    private final ForumMapper forumMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ForumDTO createForum(ForumCreateDTO dto) {
        ForumTopic topic = forumTopicService.getById(dto.topicId());
        User author = userService.getCurrentUser();

        String slug = generateUniqueSlug(dto.title());

        Forum forum = new Forum();
        forum.setTopic(topic);
        forum.setCreatedBy(author);
        forum.setTitle(dto.title());
        forum.setSlug(slug);
        forum.setSortPosition(dto.sortPosition());
        forum.setOpen(dto.open());
        forum.setPermanent(dto.permanent());

        forumRepository.save(forum);

        eventPublisher.publishEvent(new ForumCreateEvent(forum));

        return forumMapper.toDTO(forum);
    }

    @Transactional
    public ForumDTO updateForum(Long id, ForumUpdateDTO dto) {
        Forum forum = getById(id);

        if (dto.topicId() != null) {
            ForumTopic topic = forumTopicService.getById(dto.topicId());
            forum.setTopic(topic);
        }

        if (dto.title() != null) {
            String slug = generateUniqueSlug(dto.title());
            forum.setSlug(slug);
        }

        forumMapper.updateFromDTO(forum, dto);

        eventPublisher.publishEvent(new ForumUpdateEvent(forum));

        return forumMapper.toDTO(forum);
    }

    @Transactional
    public void deleteForum(Long id) {
        Forum forum = getById(id);

        if (forum.isPermanent()) {
            throw new InvalidOperationException("Permanent forums cannot be deleted");
        }

        forumRepository.delete(forum);

        eventPublisher.publishEvent(new ForumDeleteEvent(forum));
    }

    @Transactional
    public ForumDTO openForum(Long id) {
        Forum forum = getById(id);
        forum.setOpen(true);

        eventPublisher.publishEvent(new ForumUpdateEvent(forum));

        return forumMapper.toDTO(forum);
    }

    @Transactional
    public ForumDTO closeForum(Long id) {
        Forum forum = getById(id);

        if (forum.isPermanent()) {
            throw new InvalidOperationException("Permanent forums cannot be closed");
        }

        forum.setOpen(false);

        eventPublisher.publishEvent(new ForumUpdateEvent(forum));

        return forumMapper.toDTO(forum);
    }

    @Transactional(readOnly = true)
    public ForumDTO getForumById(Long id) {
        return forumMapper.toDTO(getById(id));
    }

    @Transactional(readOnly = true)
    public List<ForumDTO> getAllForums() {
        return forumRepository.findAllByOrderBySortPositionAsc()
                .stream()
                .map(forumMapper::toDTO)
                .toList();
    }

    public Forum getById(Long id) {
        return forumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.FORUM, "id", Long.toString(id)));
    }

    // Convierte el título en un slug URL-safe y garantiza su unicidad agregando un sufijo numérico si es necesario
    private String generateUniqueSlug(String title) {
        String normalized = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String base = NON_ALPHANUMERIC.matcher(normalized.toLowerCase(Locale.ROOT)).replaceAll("-")
                .replaceAll("^-|-$", "");

        if (!forumRepository.existsBySlug(base)) {
            return base;
        }

        int suffix = 1;
        String candidate;
        do {
            candidate = base + "-" + suffix;
            suffix++;
        } while (forumRepository.existsBySlug(candidate));

        return candidate;
    }
}
