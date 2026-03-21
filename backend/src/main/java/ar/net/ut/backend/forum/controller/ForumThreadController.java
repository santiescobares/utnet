package ar.net.ut.backend.forum.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.forum.service.ForumThreadService;
import ar.net.ut.backend.forum.dto.thread.ForumThreadCreateDTO;
import ar.net.ut.backend.forum.dto.thread.ForumThreadDTO;
import ar.net.ut.backend.forum.dto.thread.ForumThreadUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/forum-threads")
@RequiredArgsConstructor
public class ForumThreadController {

    private final ForumThreadService forumThreadService;

    @PostMapping
    public ResponseEntity<ForumThreadDTO> createThread(@RequestBody @Valid ForumThreadCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(forumThreadService.createThread(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumThreadDTO> updateThread(
            @PathVariable Long id,
            @RequestBody @Valid ForumThreadUpdateDTO dto
    ) {
        return ResponseEntity.ok(forumThreadService.updateThread(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThread(@PathVariable Long id) {
        forumThreadService.deleteThread(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ForumThreadDTO>> getThreadsByForum(@RequestParam Long forumId) {
        return ResponseEntity.ok(forumThreadService.getThreadsByForum(forumId));
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<List<ForumThreadDTO>> getRepliesByThread(@PathVariable Long id) {
        return ResponseEntity.ok(forumThreadService.getRepliesByThread(id));
    }
}
