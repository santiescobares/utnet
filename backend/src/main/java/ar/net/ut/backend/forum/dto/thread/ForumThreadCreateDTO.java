package ar.net.ut.backend.forum.dto.thread;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ForumThreadCreateDTO(
        @NotNull(message = "Discussion ID is required")
        Long discussionId,

        Long rootId,

        @NotBlank(message = "Content is required")
        @Size(max = 5000, message = "Content is too long")
        String content
) {
}
