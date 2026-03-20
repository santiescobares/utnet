package ar.net.ut.backend.punishment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PunishmentRepository extends JpaRepository<Punishment, Long> {

    List<Punishment> findAllByUserId(UUID userId);

    boolean existsByUserIdAndExpirationDateAfter(UUID userId, LocalDateTime dateTime);

    Optional<Punishment> findFirstByUserIdAndExpirationDateAfterOrderByExpirationDateDesc(
            UUID userId,
            LocalDateTime dateTime
    );
}
