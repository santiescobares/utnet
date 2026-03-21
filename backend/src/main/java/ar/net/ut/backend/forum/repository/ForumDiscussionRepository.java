package ar.net.ut.backend.forum.repository;

import ar.net.ut.backend.forum.ForumDiscussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ForumDiscussionRepository extends JpaRepository<ForumDiscussion, Long> {

    boolean existsBySlug(String slug);

    Optional<ForumDiscussion> findBySlug(String slug);

    Page<ForumDiscussion> findAllByOrderBySortPositionAsc(Pageable pageable);

    int countAllByCreatedById(UUID createdById);
}
