package ar.net.ut.backend.user;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.dto.interaction.UserInteractionCreateDTO;
import ar.net.ut.backend.user.dto.interaction.UserInteractionDTO;
import ar.net.ut.backend.user.entity.User;
import ar.net.ut.backend.user.entity.UserInteraction;
import ar.net.ut.backend.user.event.interaction.UserInteractionCreateEvent;
import ar.net.ut.backend.user.event.interaction.UserInteractionDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInteractionService {

    private final UserInteractionRepository interactionRepository;
    private final UserInteractionMapper interactionMapper;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserInteractionDTO createInteraction(UserInteractionCreateDTO dto) {
        User currentUser = userService.getCurrentUser();

        ResourceType resourceType = dto.resourceType();
        String resourceId = dto.resourceId();
        UserInteraction.Type type = dto.type();

        if (interactionRepository.existsByUser_IdAndTypeAndResourceTypeAndResourceId(
                currentUser.getId(), type, resourceType, resourceId)) {
            throw new InvalidOperationException("Interaction of type=" + type + " already exists for this resource");
        }

        // LIKE and DISLIKE are mutually exclusive for the same resource
        if (type == UserInteraction.Type.LIKE) {
            if (interactionRepository.existsByUser_IdAndTypeAndResourceTypeAndResourceId(
                    currentUser.getId(), UserInteraction.Type.DISLIKE, resourceType, resourceId)) {
                throw new InvalidOperationException("Cannot add LIKE: a DISLIKE already exists for this resource");
            }
        } else if (type == UserInteraction.Type.DISLIKE) {
            if (interactionRepository.existsByUser_IdAndTypeAndResourceTypeAndResourceId(
                    currentUser.getId(), UserInteraction.Type.LIKE, resourceType, resourceId)) {
                throw new InvalidOperationException("Cannot add DISLIKE: a LIKE already exists for this resource");
            }
        }

        UserInteraction interaction = new UserInteraction();
        interaction.setType(type);
        interaction.setResourceType(resourceType);
        interaction.setResourceId(resourceId);
        interaction.setUser(currentUser);

        interactionRepository.save(interaction);

        eventPublisher.publishEvent(new UserInteractionCreateEvent(interaction));

        return interactionMapper.toDTO(interaction);
    }

    @Transactional(readOnly = true)
    public List<UserInteractionDTO> getMyInteractions() {
        User currentUser = userService.getCurrentUser();
        return interactionMapper.toDTOList(interactionRepository.findAllByUser_Id(currentUser.getId()));
    }

    @Transactional
    public void deleteInteraction(Long id) {
        User currentUser = userService.getCurrentUser();

        UserInteraction interaction = interactionRepository.findByIdAndUser_Id(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER_INTERACTION, "id", Long.toString(id)));

        interactionRepository.delete(interaction);

        eventPublisher.publishEvent(new UserInteractionDeleteEvent(interaction));
    }
}
