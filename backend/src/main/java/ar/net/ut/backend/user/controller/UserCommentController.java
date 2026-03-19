package ar.net.ut.backend.user.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.user.service.UserCommentService;
import ar.net.ut.backend.user.dto.comment.UserCommentCreateDTO;
import ar.net.ut.backend.user.dto.comment.UserCommentDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/users/{userId}/comments")
@RequiredArgsConstructor
public class UserCommentController {

    private final UserCommentService commentService;

    @PostMapping
    public ResponseEntity<UserCommentDTO> createComment(
            @PathVariable UUID userId,
            @RequestBody @Valid UserCommentCreateDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<UserCommentDTO>> getCommentsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(commentService.getCommentsByUser(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID userId, @PathVariable Long id) {
        commentService.deleteComment(userId, id);
        return ResponseEntity.noContent().build();
    }
}
