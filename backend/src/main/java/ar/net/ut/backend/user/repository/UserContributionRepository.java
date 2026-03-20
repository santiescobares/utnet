package ar.net.ut.backend.user.repository;

import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.UserContribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserContributionRepository extends JpaRepository<UserContribution, Long> {

    void deleteByUserIdAndResourceTypeAndResourceId(UUID userId, ResourceType resourceType, String resourceId);
}
