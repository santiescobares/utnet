package ar.net.ut.backend.user;

import ar.net.ut.backend.user.entity.UserContribution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserContributionRepository extends JpaRepository<UserContribution, Long> {

    List<UserContribution> findAllByUser_Id(UUID userId);
}
