package ar.net.ut.backend.user.repository;

import ar.net.ut.backend.user.UserContribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserContributionRepository extends JpaRepository<UserContribution, Long> {

    List<UserContribution> findAllByUserId(UUID userId);
}
