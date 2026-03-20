package ar.net.ut.backend.user.service;

import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.InvalidOperationException;
import ar.net.ut.backend.exception.impl.ResourceAlreadyExistsException;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.model.Interactionable;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.mapper.UserInteractionMapper;
import ar.net.ut.backend.user.dto.interaction.UserInteractionDTO;
import ar.net.ut.backend.user.UserInteraction;
import ar.net.ut.backend.user.event.interaction.UserInteractionCreateEvent;
import ar.net.ut.backend.user.event.interaction.UserInteractionDeleteEvent;
import ar.net.ut.backend.user.repository.UserInteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserInteractionService {

    private final UserService userService;

    private final UserInteractionRepository interactionRepository;

    private final UserInteractionMapper interactionMapper;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createInteraction(ResourceType resourceType, String resourceId, UserInteraction.Type type) {
        Interactionable interactionable = Interactionable.getSafeByResource(resourceType);
        UUID userId = RequestContextHolder.getCurrentUser();

        if (interactionRepository.existsByUserIdAndTypeAndResourceTypeAndResourceId(userId, type, resourceType, resourceId)) {
            throw new ResourceAlreadyExistsException(
                    ResourceType.USER_INTERACTION, "type + resourceType + resourceId", type + " + " + resourceType + " + " + resourceId
            );
        }

        UserInteraction.Type oppositeInteraction = type.opposite();
        if (oppositeInteraction != null && interactionRepository.existsByUserIdAndTypeAndResourceTypeAndResourceId(
                userId, oppositeInteraction, resourceType, resourceId
        )) {
            throw new InvalidOperationException("Can't add " + type + ": a " + oppositeInteraction + " already exists for that resource");
        }

        User user = userService.getCurrentUser();
        UserInteraction interaction = new UserInteraction();
        interaction.setUser(user);
        interaction.setType(type);
        interaction.setResourceType(resourceType);
        interaction.setResourceId(resourceId);

        interactionRepository.save(interaction);

        interactionable.onInteractionCreate(resourceId, type);

        eventPublisher.publishEvent(new UserInteractionCreateEvent(interaction));
    }

    @Transactional
    public void deleteInteraction(Long id) {
        UserInteraction interaction = interactionRepository.findByIdAndUserId(id, RequestContextHolder.getCurrentUser())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER_INTERACTION, "id", Long.toString(id)));

        interactionRepository.delete(interaction);

        Interactionable interactionable = Interactionable.getByResource(interaction.getResourceType());
        if (interactionable != null) {
            interactionable.onInteractionDelete(Long.toString(id), interaction.getType());
        }

        eventPublisher.publishEvent(new UserInteractionDeleteEvent(interaction));
    }

    @Transactional(readOnly = true)
    public List<UserInteractionDTO> getMyInteractions(ResourceType resourceType, String resourceId) {
        UUID userId = RequestContextHolder.getCurrentUser();
        List<UserInteraction> interactions;

        if (resourceId != null) {
            interactions = interactionRepository.findAllByUserIdAndResourceTypeAndResourceId(userId, resourceType, resourceId);
        } else {
            interactions = interactionRepository.findAllByUserIdAndResourceType(userId, resourceType);
        }

        return interactionMapper.toDTOList(interactions);
    }
}
