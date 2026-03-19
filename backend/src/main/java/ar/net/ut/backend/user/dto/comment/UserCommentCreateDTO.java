package ar.net.ut.backend.user.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCommentCreateDTO(
        @NotBlank
        @Size(max = 500)
        String content
) {
}
