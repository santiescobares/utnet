package ar.net.ut.backend.forum.service;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.forum.repository.ForumThreadRepository;
import ar.net.ut.backend.forum.dto.thread.ForumThreadCreateDTO;
import ar.net.ut.backend.forum.dto.thread.ForumThreadDTO;
import ar.net.ut.backend.forum.dto.thread.ForumThreadUpdateDTO;
import ar.net.ut.backend.forum.Forum;
import ar.net.ut.backend.forum.ForumThread;
import ar.net.ut.backend.forum.event.ForumThreadCreateEvent;
import ar.net.ut.backend.forum.event.ForumThreadDeleteEvent;
import ar.net.ut.backend.forum.event.ForumThreadUpdateEvent;
import ar.net.ut.backend.forum.mapper.ForumThreadMapper;
import ar.net.ut.backend.user.service.UserService;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumThreadService {

    private final ForumThreadRepository forumThreadRepository;
    private final ForumService forumService;
    private final UserService userService;
    private final ForumThreadMapper forumThreadMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ForumThreadDTO createThread(ForumThreadCreateDTO dto) {
        Forum forum = forumService.getById(dto.forumId());

        if (!forum.isOpen()) {
            throw new InvalidOperationException("Cannot post in a closed forum");
        }

        User author = userService.getCurrentUser();

        ForumThread thread = new ForumThread();
        thread.setContent(dto.content());

        List<String> imageKeys = dto.imageKeys();
        if (imageKeys != null && !imageKeys.isEmpty()) {
            thread.setImageKeys(List.copyOf(imageKeys));
        }

        if (dto.rootId() != null) {
            ForumThread root = getById(dto.rootId());

            if (!root.getForum().getId().equals(forum.getId())) {
                throw new InvalidOperationException("Root thread does not belong to the specified forum");
            }

            forum.addThread(thread);
            root.addReply(thread);
        } else {
            forum.addThread(thread);
        }

        thread.setPostedBy(author);
        forumThreadRepository.save(thread);

        eventPublisher.publishEvent(new ForumThreadCreateEvent(thread));

        return forumThreadMapper.toDTO(thread);
    }

    @Transactional
    public ForumThreadDTO updateThread(Long id, ForumThreadUpdateDTO dto) {
        ForumThread thread = getById(id);
        User currentUser = userService.getCurrentUser();

        if (!thread.getPostedBy().getId().equals(currentUser.getId())) {
            throw new InvalidOperationException("Only the author can edit this thread");
        }

        forumThreadMapper.updateFromDTO(thread, dto);

        eventPublisher.publishEvent(new ForumThreadUpdateEvent(thread));

        return forumThreadMapper.toDTO(thread);
    }

    @Transactional
    public void deleteThread(Long id) {
        ForumThread thread = getById(id);
        User currentUser = userService.getCurrentUser();

        boolean isAuthor = thread.getPostedBy().getId().equals(currentUser.getId());
        Role role = currentUser.getRole();
        boolean isModerator = role == Role.CONTRIBUTOR_3 || role == Role.ADMINISTRATOR;

        if (!isAuthor && !isModerator) {
            throw new InvalidOperationException("You do not have permission to delete this thread");
        }

        forumThreadRepository.delete(thread);

        eventPublisher.publishEvent(new ForumThreadDeleteEvent(thread));
    }

    @Transactional(readOnly = true)
    public List<ForumThreadDTO> getThreadsByForum(Long forumId) {
        forumService.getById(forumId);
        return forumThreadRepository.findAllByForumIdAndRootIsNullOrderByCreatedAtAsc(forumId)
                .stream()
                .map(forumThreadMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ForumThreadDTO> getRepliesByThread(Long threadId) {
        getById(threadId);
        return forumThreadRepository.findAllByRootIdOrderByCreatedAtAsc(threadId)
                .stream()
                .map(forumThreadMapper::toDTO)
                .toList();
    }

    public ForumThread getById(Long id) {
        return forumThreadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.FORUM_THREAD, "id", Long.toString(id)));
    }
}
