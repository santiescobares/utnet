package ar.net.ut.backend.user;

import ar.net.ut.backend.user.entity.UserComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCommentRepository extends JpaRepository<UserComment, Long> {

    List<UserComment> findAllByResource_Id(UUID resourceId);

    Optional<UserComment> findByIdAndPostedBy_Id(Long id, UUID postedById);
}
