package ar.net.ut.backend.user;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.entity.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, Long> {

    List<UserInteraction> findAllByUser_Id(UUID userId);

    boolean existsByUser_IdAndTypeAndResourceTypeAndResourceId(
            UUID userId, UserInteraction.Type type, ResourceType resourceType, String resourceId);

    Optional<UserInteraction> findByIdAndUser_Id(Long id, UUID userId);
}
