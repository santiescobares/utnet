package ar.net.ut.backend.user.repository;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

    List<UserInteraction> findAllByUserIdAndResourceTypeAndResourceId(UUID userId, ResourceType resourceType, String resourceId);

    List<UserInteraction> findAllByUserIdAndResourceType(UUID userId, ResourceType resourceType);

    @Query("SELECT ui.user.id FROM UserInteraction ui WHERE ui.resourceType = :resourceType AND ui.resourceId = :resourceId")
    List<UUID> findAllInteractorsOn(@Param("resourceType") ResourceType resourceType, @Param("resourceId") String resourceId);

    boolean existsByUserIdAndTypeAndResourceTypeAndResourceId(
            UUID userId,
            UserInteraction.Type type,
            ResourceType resourceType,
            String resourceId
    );

    UserInteraction deleteByUserIdAndTypeAndResourceTypeAndResourceId(
            UUID userId,
            UserInteraction.Type type,
            ResourceType resourceType,
            String resourceId
    );

    boolean existsByUserIdAndResourceTypeAndResourceId(UUID userId, ResourceType type, String resourceId);

    Optional<UserInteraction> findByIdAndUserId(Long id, UUID userId);
}
