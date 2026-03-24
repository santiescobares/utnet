package ar.net.ut.backend.forum.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.forum.dto.ForumDiscussionDTO;
import ar.net.ut.backend.forum.dto.ForumDiscussionUpdateDTO;
import ar.net.ut.backend.forum.service.ForumDiscussionService;
import ar.net.ut.backend.forum.dto.ForumDiscussionCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/forum-discussions")
@RequiredArgsConstructor
public class ForumDiscussionController {

    private final ForumDiscussionService forumDiscussionService;

    @PostMapping
    public ResponseEntity<ForumDiscussionDTO> createForumDiscussion(@RequestBody @Valid ForumDiscussionCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(forumDiscussionService.createDiscussion(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumDiscussionDTO> updateForumDiscussion(
            @PathVariable Long id,
            @RequestBody @Valid ForumDiscussionUpdateDTO dto
    ) {
        return ResponseEntity.ok(forumDiscussionService.updateDiscussion(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForumDiscussion(@PathVariable Long id) {
        forumDiscussionService.deleteDiscussion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ForumDiscussionDTO>> getAllForumDiscussions(
            @RequestParam(name = "")
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(forumDiscussionService.getAllDiscussions(pageable));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ForumDiscussionDTO> getForumDiscussionById(@PathVariable Long id) {
        return ResponseEntity.ok(forumDiscussionService.getDiscussionById(id));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ForumDiscussionDTO> getForumDiscussionBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(forumDiscussionService.getDiscussionBySlug(slug));
    }

    @PatchMapping("/{id}/open")
    public ResponseEntity<ForumDiscussionDTO> openForumDiscussion(@PathVariable Long id) {
        return ResponseEntity.ok(forumDiscussionService.openDiscussion(id));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ForumDiscussionDTO> closeForumDiscussion(@PathVariable Long id) {
        return ResponseEntity.ok(forumDiscussionService.closeDiscussion(id));
    }
}
