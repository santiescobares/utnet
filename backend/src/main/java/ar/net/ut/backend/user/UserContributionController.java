package ar.net.ut.backend.user;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.user.dto.contribution.UserContributionCreateDTO;
import ar.net.ut.backend.user.dto.contribution.UserContributionDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/users")
@RequiredArgsConstructor
public class UserContributionController {

    private final UserContributionService contributionService;

    @PostMapping("/contributions")
    public ResponseEntity<UserContributionDTO> createContribution(@RequestBody @Valid UserContributionCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contributionService.createContribution(dto));
    }

    @GetMapping("/{userId}/contributions")
    public ResponseEntity<List<UserContributionDTO>> getContributionsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(contributionService.getContributionsByUser(userId));
    }

    @DeleteMapping("/contributions/{id}")
    public ResponseEntity<Void> deleteContribution(@PathVariable Long id) {
        contributionService.deleteContribution(id);
        return ResponseEntity.noContent().build();
    }
}
