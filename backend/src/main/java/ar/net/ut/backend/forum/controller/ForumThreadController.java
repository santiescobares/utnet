package ar.net.ut.backend.forum.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.forum.service.ForumThreadService;
import ar.net.ut.backend.forum.dto.thread.ForumThreadCreateDTO;
import ar.net.ut.backend.forum.dto.thread.ForumThreadDTO;
import ar.net.ut.backend.forum.dto.thread.ForumThreadUpdateDTO;
import ar.net.ut.backend.user.UserInteraction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/forum-threads")
@RequiredArgsConstructor
public class ForumThreadController {

    private final ForumThreadService forumThreadService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ForumThreadDTO> createThread(
            @RequestPart("data") @Valid ForumThreadCreateDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(forumThreadService.createThread(dto, images));
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

    @PatchMapping("/{id}/add-interaction")
    public ResponseEntity<Void> addThreadInteraction(
            @PathVariable Long id,
            @RequestParam(name = "type") UserInteraction.Type type
    ) {
        forumThreadService.addThreadInteraction(id, type);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/remove-interaction")
    public ResponseEntity<Void> removeThreadInteraction(
            @PathVariable Long id,
            @RequestParam(name = "type") UserInteraction.Type type
    ) {
        forumThreadService.removeThreadInteraction(id, type);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ForumThreadDTO>> getThreadsByForum(
            @RequestParam Long forumId,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(forumThreadService.getThreadsByForum(forumId, pageable));
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<Page<ForumThreadDTO>> getRepliesByThread(
            @PathVariable Long id,
            @PageableDefault(sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(forumThreadService.getRepliesByThread(id, pageable));
    }
}
