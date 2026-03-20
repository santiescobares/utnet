package ar.net.ut.backend.user.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCommentCreateDTO(
        @NotBlank(message = "Content is required")
        @Size(max = 500, message = "Content is too long")
        String content
) {
}
