package ar.net.ut.backend.forum.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForumTopicCreateDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 30, message = "Name is too long")
        String name
) {
}
