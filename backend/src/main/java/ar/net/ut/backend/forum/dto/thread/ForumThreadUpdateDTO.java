package ar.net.ut.backend.forum.dto.thread;

import jakarta.validation.constraints.Size;

import java.util.List;

public record ForumThreadUpdateDTO(
        @Size(max = 5000, message = "Content is too long")
        String content,

        List<String> imageKeys
) {
}
