package ar.net.ut.backend.user;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.user.dto.interaction.UserInteractionCreateDTO;
import ar.net.ut.backend.user.dto.interaction.UserInteractionDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/users/interactions")
@RequiredArgsConstructor
public class UserInteractionController {

    private final UserInteractionService interactionService;

    @PostMapping
    public ResponseEntity<UserInteractionDTO> createInteraction(@RequestBody @Valid UserInteractionCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interactionService.createInteraction(dto));
    }

    @GetMapping
    public ResponseEntity<List<UserInteractionDTO>> getMyInteractions() {
        return ResponseEntity.ok(interactionService.getMyInteractions());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInteraction(@PathVariable Long id) {
        interactionService.deleteInteraction(id);
        return ResponseEntity.noContent().build();
    }
}
