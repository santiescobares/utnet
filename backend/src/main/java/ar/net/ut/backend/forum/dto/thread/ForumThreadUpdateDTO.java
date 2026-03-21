package ar.net.ut.backend.forum.dto.thread;

import jakarta.validation.constraints.Size;

public record ForumThreadUpdateDTO(
        @Size(max = 5000, message = "Content is too long")
        String content
) {
}
