package ar.net.ut.backend.user.service;

import ar.net.ut.backend.context.RequestContextData;
import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.UserInteraction;
import ar.net.ut.backend.user.event.comment.UserCommentAddInteractionEvent;
import ar.net.ut.backend.user.event.comment.UserCommentRemoveInteractionEvent;
import ar.net.ut.backend.user.mapper.UserCommentMapper;
import ar.net.ut.backend.user.dto.comment.UserCommentCreateDTO;
import ar.net.ut.backend.user.dto.comment.UserCommentDTO;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.UserComment;
import ar.net.ut.backend.user.enums.Role;
import ar.net.ut.backend.user.event.comment.UserCommentCreateEvent;
import ar.net.ut.backend.user.event.comment.UserCommentDeleteEvent;
import ar.net.ut.backend.user.repository.UserCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommentService {

    private final UserService userService;

    private final UserCommentRepository commentRepository;

    private final UserCommentMapper commentMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserCommentDTO createComment(UUID targetUserId, UserCommentCreateDTO dto) {
        User targetUser = userService.getById(targetUserId);
        User currentUser = userService.getCurrentUser();

        if (targetUser.equals(currentUser)) {
            throw new InvalidOperationException("You can't create a comment to yourself");
        }

        UserComment comment = new UserComment();
        comment.setResource(targetUser);
        comment.setPostedBy(currentUser);
        comment.setContent(dto.content());

        commentRepository.save(comment);

        eventPublisher.publishEvent(new UserCommentCreateEvent(comment));

        return commentMapper.toDTO(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        RequestContextData session = RequestContextHolder.getCurrentSession();
        UserComment comment = getById(id);

        boolean isAuthor = comment.getPostedBy().getId().equals(session.userId());
        boolean isAdmin = session.role() == Role.ADMINISTRATOR;

        if (!isAuthor && !isAdmin) {
            throw new InvalidOperationException("You can't delete that comment");
        }

        commentRepository.delete(comment);

        eventPublisher.publishEvent(new UserCommentDeleteEvent(comment));
    }

    @Transactional(readOnly = true)
    public Page<UserCommentDTO> getCommentsDTOByUser(UUID userId, Pageable pageable) {
        return commentRepository.findAllByResourceId(userId, pageable).map(commentMapper::toDTO);
    }

    @Transactional
    public void addCommentInteraction(Long id, UserInteraction.Type type) {
        if (type != UserInteraction.Type.LIKE && type != UserInteraction.Type.DISLIKE) {
            throw new IllegalArgumentException("Invalid interaction type for comment");
        }

        UserComment comment = getById(id);

        if (type == UserInteraction.Type.LIKE) {
            comment.addLike();
        } else {
            comment.addDislike();
        }

        eventPublisher.publishEvent(new UserCommentAddInteractionEvent(comment, type));
    }

    @Transactional
    public void removeCommentInteraction(Long id, UserInteraction.Type type) {
        if (type != UserInteraction.Type.LIKE && type != UserInteraction.Type.DISLIKE) {
            throw new IllegalArgumentException("Invalid interaction type for comment");
        }

        UserComment comment = getById(id);

        if (type == UserInteraction.Type.LIKE) {
            comment.removeLike();
        } else {
            comment.removeDislike();
        }

        eventPublisher.publishEvent(new UserCommentRemoveInteractionEvent(comment, type));
    }

    public UserComment getById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER_COMMENT, "id", id.toString()));
    }
}
