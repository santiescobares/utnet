package ar.net.ut.backend.forum.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.forum.service.ForumTopicService;
import ar.net.ut.backend.forum.dto.topic.ForumTopicCreateDTO;
import ar.net.ut.backend.forum.dto.topic.ForumTopicDTO;
import ar.net.ut.backend.forum.dto.topic.ForumTopicUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/forum-topics")
@RequiredArgsConstructor
public class ForumTopicController {

    private final ForumTopicService forumTopicService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<ForumTopicDTO> createForumTopic(@RequestBody @Valid ForumTopicCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(forumTopicService.createForumTopic(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<ForumTopicDTO> updateForumTopic(
            @PathVariable Long id,
            @RequestBody @Valid ForumTopicUpdateDTO dto
    ) {
        return ResponseEntity.ok(forumTopicService.updateForumTopic(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteForumTopic(@PathVariable Long id) {
        forumTopicService.deleteForumTopic(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ForumTopicDTO>> getAllForumTopics() {
        return ResponseEntity.ok(forumTopicService.getAllForumTopics());
    }
}
