package ar.net.ut.backend.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ForumCreateDTO(
        @NotNull(message = "Topic ID is required")
        Long topicId,

        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title is too long")
        String title,

        @PositiveOrZero(message = "Sort position must be positive or zero")
        int sortPosition,

        boolean open,

        boolean permanent
) {
}
