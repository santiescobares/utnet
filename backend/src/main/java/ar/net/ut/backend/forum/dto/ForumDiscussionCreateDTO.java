package ar.net.ut.backend.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ForumDiscussionCreateDTO(
        @NotNull(message = "Topic ID is required")
        Long topicId,

        @NotBlank(message = "Title is required")
        @Size(min = 5, max = 100, message = "Title is either too short or too long")
        String title,

        boolean open,

        boolean permanent
) {
}
