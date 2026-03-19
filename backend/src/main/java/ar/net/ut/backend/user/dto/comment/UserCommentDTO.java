package ar.net.ut.backend.user.dto.comment;

import java.time.Instant;
import java.util.UUID;

public record UserCommentDTO(
        Long id,
        Instant createdAt,
        UUID resourceId,
        UUID postedById,
        String content
) {
}
