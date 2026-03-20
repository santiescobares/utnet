package ar.net.ut.backend.user.repository;

import ar.net.ut.backend.user.UserComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserCommentRepository extends JpaRepository<UserComment, Long> {

    Page<UserComment> findAllByResourceId(UUID resourceId, Pageable pageable);
}
