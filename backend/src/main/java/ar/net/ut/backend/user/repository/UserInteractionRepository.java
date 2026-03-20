package ar.net.ut.backend.user.repository;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

    List<UserInteraction> findAllByUserIdAndResourceTypeAndResourceId(UUID userId, ResourceType resourceType, String resourceId);

    List<UserInteraction> findAllByUserIdAndResourceType(UUID userId, ResourceType resourceType);

    boolean existsByUserIdAndTypeAndResourceTypeAndResourceId(
            UUID userId,
            UserInteraction.Type type,
            ResourceType resourceType,
            String resourceId
    );

    Optional<UserInteraction> findByIdAndUserId(Long id, UUID userId);
}
