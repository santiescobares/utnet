package ar.net.ut.backend.user;

import ar.net.ut.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query(value = "SELECT * FROM users WHERE email ILIKE :email", nativeQuery = true)
    Optional<User> findByEmailIncludingDeleted(@Param("email") String email);

    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findByReferralId(Long referralId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users " +
            "SET first_name = NULL, last_name = NULL, email = NULL, " +
            "birthday = NULL, google_id = NULL " +
            "WHERE deleted_at IS NOT NULL " +
            "AND deleted_at <= NOW() - INTERVAL '5 days' " +
            "AND email IS NOT NULL",
            nativeQuery = true)
    int anonymizeDeletedUsers();
}
