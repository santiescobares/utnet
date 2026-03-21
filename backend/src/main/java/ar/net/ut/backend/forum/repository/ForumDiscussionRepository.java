package ar.net.ut.backend.forum.repository;

import ar.net.ut.backend.forum.ForumDiscussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface ForumDiscussionRepository extends JpaRepository<ForumDiscussion, Long> {

    boolean existsBySlug(String slug);

    Optional<ForumDiscussion> findBySlug(String slug);

    Page<ForumDiscussion> findAllByOrderBySortPositionAsc(Pageable pageable);

    int countAllByCreatedById(UUID createdById);

    @Modifying
    @Query("DELETE FROM ForumDiscussion fd WHERE fd.createdAt < :cutoff AND NOT fd.permanent")
    int deleteByCreatedAtBefore(@Param("cutoff") Instant cutoff);
}
