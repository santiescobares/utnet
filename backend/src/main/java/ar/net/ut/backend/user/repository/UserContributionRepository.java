package ar.net.ut.backend.user.repository;

import ar.net.ut.backend.user.UserContribution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserContributionRepository extends JpaRepository<UserContribution, Long> {
}
