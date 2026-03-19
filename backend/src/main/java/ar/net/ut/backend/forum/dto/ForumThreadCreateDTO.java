package ar.net.ut.backend.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ForumThreadCreateDTO(
        @NotNull(message = "Forum ID is required")
        Long forumId,

        Long rootId,

        @NotBlank(message = "Content is required")
        @Size(max = 5000, message = "Content is too long")
        String content,

        List<String> imageKeys
) {
}
