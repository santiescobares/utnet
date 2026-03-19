package ar.net.ut.backend.user.repository;

import ar.net.ut.backend.user.UserComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCommentRepository extends JpaRepository<UserComment, Long> {

    List<UserComment> findAllByResourceId(UUID resourceId);

    Optional<UserComment> findByIdAndPostedById(Long id, UUID postedById);
}
