package ar.net.ut.backend.forum;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.forum.dto.ForumCreateDTO;
import ar.net.ut.backend.forum.dto.ForumDTO;
import ar.net.ut.backend.forum.dto.ForumUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/forums")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    @PostMapping
    public ResponseEntity<ForumDTO> createForum(@RequestBody @Valid ForumCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(forumService.createForum(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumDTO> updateForum(
            @PathVariable Long id,
            @RequestBody @Valid ForumUpdateDTO dto
    ) {
        return ResponseEntity.ok(forumService.updateForum(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForum(@PathVariable Long id) {
        forumService.deleteForum(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ForumDTO>> getAllForums() {
        return ResponseEntity.ok(forumService.getAllForums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumDTO> getForumById(@PathVariable Long id) {
        return ResponseEntity.ok(forumService.getForumById(id));
    }

    @PatchMapping("/{id}/open")
    public ResponseEntity<ForumDTO> openForum(@PathVariable Long id) {
        return ResponseEntity.ok(forumService.openForum(id));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<ForumDTO> closeForum(@PathVariable Long id) {
        return ResponseEntity.ok(forumService.closeForum(id));
    }
}
