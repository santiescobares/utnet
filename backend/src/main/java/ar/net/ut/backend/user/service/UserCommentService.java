package ar.net.ut.backend.user.service;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.mapper.UserCommentMapper;
import ar.net.ut.backend.user.dto.comment.UserCommentCreateDTO;
import ar.net.ut.backend.user.dto.comment.UserCommentDTO;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.UserComment;
import ar.net.ut.backend.user.enums.Role;
import ar.net.ut.backend.user.event.comment.UserCommentCreateEvent;
import ar.net.ut.backend.user.event.comment.UserCommentDeleteEvent;
import ar.net.ut.backend.user.repository.UserCommentRepository;
import ar.net.ut.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommentService {

    private final UserCommentRepository commentRepository;
    private final UserCommentMapper commentMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserCommentDTO createComment(UUID targetUserId, UserCommentCreateDTO dto) {
        User targetUser = userService.getById(targetUserId);
        User currentUser = userService.getCurrentUser();

        UserComment comment = new UserComment();
        comment.setContent(dto.content());
        comment.setPostedBy(currentUser);
        targetUser.addComment(comment);

        userRepository.save(targetUser);

        eventPublisher.publishEvent(new UserCommentCreateEvent(comment));

        return commentMapper.toDTO(comment);
    }

    @Transactional(readOnly = true)
    public List<UserCommentDTO> getCommentsByUser(UUID userId) {
        userService.getById(userId);
        return commentMapper.toDTOList(commentRepository.findAllByResourceId(userId));
    }

    @Transactional
    public void deleteComment(UUID userId, Long commentId) {
        User currentUser = userService.getCurrentUser();

        UserComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER_COMMENT, "id", commentId.toString()));

        boolean isAuthor = comment.getPostedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMINISTRATOR;

        if (!isAuthor && !isAdmin) {
            throw new InvalidOperationException("Not authorized to delete comment with id=" + commentId);
        }

        commentRepository.delete(comment);

        eventPublisher.publishEvent(new UserCommentDeleteEvent(comment));
    }
}
