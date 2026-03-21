package ar.net.ut.backend.forum.service;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.config.S3Config;
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
import ar.net.ut.backend.service.StorageService;
import ar.net.ut.backend.user.UserComment;
import ar.net.ut.backend.user.UserInteraction;
import ar.net.ut.backend.user.event.comment.UserCommentAddInteractionEvent;
import ar.net.ut.backend.user.event.comment.UserCommentRemoveInteractionEvent;
import ar.net.ut.backend.user.service.UserService;
import ar.net.ut.backend.user.enums.Role;
import ar.net.ut.backend.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ForumThreadService {

    private static final int MAX_FILES_PER_THREAD = 3;
    private static final Set<String> ALLOWED_IMAGE_FORMATS = Set.of("png", "jpg", "jpeg");
    private static final long MAX_IMAGES_SIZE = 10_485_760; // In bytes

    private final ForumDiscussionService forumDiscussionService;
    private final UserService userService;
    private final StorageService storageService;

    private final ForumThreadRepository forumThreadRepository;

    private final ForumThreadMapper forumThreadMapper;

    private final ApplicationEventPublisher eventPublisher;

    private final S3Config s3Config;

    public ForumThreadDTO createThread(ForumThreadCreateDTO dto, List<MultipartFile> images) {
        ForumDiscussion forumDiscussion = forumDiscussionService.getById(dto.discussionId());
        if (!forumDiscussion.isOpen()) {
            throw new InvalidOperationException("Can't post in a closed forum discussion");
        }

        boolean hasImages = images != null && !images.isEmpty();
        if (hasImages) {
            if (images.size() > MAX_FILES_PER_THREAD) {
                throw new InvalidOperationException("You can only upload up to " + MAX_FILES_PER_THREAD + " images per thread");
            }

            long totalSize = 0;
            for (MultipartFile image : images) {
                FileUtil.validateExtension(image, ALLOWED_IMAGE_FORMATS);
                totalSize += image.getSize();
            }
            FileUtil.validateSize(totalSize, MAX_IMAGES_SIZE);
        }

        ForumThread thread = new ForumThread();
        thread.setContent(dto.content());

        if (dto.rootId() != null) {
            ForumThread root = getById(dto.rootId());

            if (forumDiscussion.getId().equals(dto.rootId())) {
                throw new InvalidOperationException("What are you trying to do?...");
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

        if (hasImages) {
            List<String> imageKeys = storageService.uploadFilesInParallel(
                    images,
                    s3Config.getPublicBucket(),
                    Global.R2.FORUM_THREAD_IMAGES_PATH.toString()
            );
            thread.setImageKeys(imageKeys);
        }

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
