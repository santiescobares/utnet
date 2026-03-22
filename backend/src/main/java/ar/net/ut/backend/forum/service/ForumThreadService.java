package ar.net.ut.backend.forum.service;

import ar.net.ut.backend.context.RequestContextData;
import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.forum.event.thread.*;
import ar.net.ut.backend.forum.repository.ForumThreadRepository;
import ar.net.ut.backend.forum.dto.thread.ForumThreadCreateDTO;
import ar.net.ut.backend.forum.dto.thread.ForumThreadDTO;
import ar.net.ut.backend.forum.dto.thread.ForumThreadUpdateDTO;
import ar.net.ut.backend.forum.ForumDiscussion;
import ar.net.ut.backend.forum.ForumThread;
import ar.net.ut.backend.forum.mapper.ForumThreadMapper;
import ar.net.ut.backend.user.UserInteraction;
import ar.net.ut.backend.user.service.UserService;
import ar.net.ut.backend.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForumThreadService {

    private final ForumDiscussionService forumDiscussionService;
    private final UserService userService;

    private final ForumThreadRepository forumThreadRepository;

    private final ForumThreadMapper forumThreadMapper;

    private final ApplicationEventPublisher eventPublisher;

    public ForumThreadDTO createThread(ForumThreadCreateDTO dto) {
        ForumDiscussion forumDiscussion = forumDiscussionService.getById(dto.discussionId());
        if (!forumDiscussion.isOpen()) {
            throw new InvalidOperationException("Can't post in a closed forum discussion");
        }

        ForumThread thread = new ForumThread();
        thread.setContent(dto.content());

        if (dto.rootId() != null) {
            ForumThread root = getById(dto.rootId());

            if (root.getRoot() != null) {
                throw new InvalidOperationException("Invalid thread root");
            }
            if (!root.getDiscussion().getId().equals(forumDiscussion.getId())) {
                throw new InvalidOperationException("Root thread doesn't belong to the specified forum");
            }

            forumDiscussion.addThread(thread);
            root.addReply(thread);
        } else {
            forumDiscussion.addThread(thread);
        }

        thread.setPostedBy(userService.getCurrentUser());

        forumThreadRepository.save(thread);

        eventPublisher.publishEvent(new ForumThreadCreateEvent(thread));

        return forumThreadMapper.toDTO(thread);
    }

    @Transactional
    public ForumThreadDTO updateThread(Long id, ForumThreadUpdateDTO dto) {
        ForumThread thread = getById(id);

        if (!thread.getPostedBy().getId().equals(RequestContextHolder.getCurrentSession().userId())) {
            throw new InvalidOperationException("You can't edit that thread");
        }

        forumThreadMapper.updateFromDTO(thread, dto);

        eventPublisher.publishEvent(new ForumThreadUpdateEvent(thread));

        return forumThreadMapper.toDTO(thread);
    }

    @Transactional
    public void deleteThread(Long id) {
        RequestContextData session = RequestContextHolder.getCurrentSession();
        ForumThread thread = getById(id);

        boolean isAuthor = thread.getPostedBy().getId().equals(session.userId());
        boolean isAdmin = session.role() == Role.ADMINISTRATOR;

        if (!isAuthor && !isAdmin) {
            throw new InvalidOperationException("You can't delete that thread");
        }

        forumThreadRepository.delete(thread);

        eventPublisher.publishEvent(new ForumThreadDeleteEvent(thread));
    }

    @Transactional
    public void addThreadInteraction(Long id, UserInteraction.Type type) {
        if (type != UserInteraction.Type.LIKE && type != UserInteraction.Type.DISLIKE) {
            throw new IllegalArgumentException("Invalid interaction type for thread");
        }

        ForumThread thread = getById(id);

        if (type == UserInteraction.Type.LIKE) {
            thread.addLike();
        } else {
            thread.addDislike();
        }

        eventPublisher.publishEvent(new ForumThreadAddInteractionEvent(thread, type));
    }

    @Transactional
    public void removeThreadInteraction(Long id, UserInteraction.Type type) {
        if (type != UserInteraction.Type.LIKE && type != UserInteraction.Type.DISLIKE) {
            throw new IllegalArgumentException("Invalid interaction type for thread");
        }

        ForumThread thread = getById(id);

        if (type == UserInteraction.Type.LIKE) {
            thread.removeLike();
        } else {
            thread.removeDislike();
        }

        eventPublisher.publishEvent(new ForumThreadRemoveInteractionEvent(thread, type));
    }

    @Transactional(readOnly = true)
    public Page<ForumThreadDTO> getThreadsByForum(Long forumId, Pageable pageable) {
        forumDiscussionService.getById(forumId);
        return forumThreadRepository.findAllByDiscussionIdAndRootIsNullOrderByCreatedAtAsc(forumId, pageable).map(forumThreadMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ForumThreadDTO> getRepliesByThread(Long threadId, Pageable pageable) {
        getById(threadId);
        return forumThreadRepository.findAllByRootIdOrderByCreatedAtAsc(threadId, pageable).map(forumThreadMapper::toDTO);
    }

    public ForumThread getById(Long id) {
        return forumThreadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.FORUM_THREAD, "id", Long.toString(id)));
    }
}
