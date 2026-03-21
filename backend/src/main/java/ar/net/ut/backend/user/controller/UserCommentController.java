package ar.net.ut.backend.user.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.user.UserInteraction;
import ar.net.ut.backend.user.service.UserCommentService;
import ar.net.ut.backend.user.dto.comment.UserCommentCreateDTO;
import ar.net.ut.backend.user.dto.comment.UserCommentDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/users/comments")
@RequiredArgsConstructor
public class UserCommentController {

    private final UserCommentService commentService;

    @PostMapping("/{userId}")
    public ResponseEntity<UserCommentDTO> createComment(
            @PathVariable UUID userId,
            @RequestBody @Valid UserCommentCreateDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(userId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/add-interaction")
    public ResponseEntity<Void> addCommentInteraction(
            @PathVariable Long id,
            @RequestParam(name = "type") UserInteraction.Type type
    ) {
        commentService.addCommentInteraction(id, type);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/remove-interaction")
    public ResponseEntity<Void> removeCommentInteraction(
            @PathVariable Long id,
            @RequestParam(name = "type") UserInteraction.Type type
    ) {
        commentService.removeCommentInteraction(id, type);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<UserCommentDTO>> getCommentsByUser(
            @PathVariable UUID userId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getCommentsDTOByUser(userId, pageable));
    }
}
