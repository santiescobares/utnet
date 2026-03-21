package ar.net.ut.backend.punishment;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.punishment.dto.PunishmentCreateDTO;
import ar.net.ut.backend.punishment.dto.PunishmentDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/punishments")
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
@RequiredArgsConstructor
public class PunishmentController {

    private final PunishmentService punishmentService;

    @PostMapping
    public ResponseEntity<PunishmentDTO> createPunishment(@RequestBody @Valid PunishmentCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(punishmentService.createPunishment(dto));
    }

    @GetMapping
    public ResponseEntity<Page<PunishmentDTO>> getAllPunishments(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(punishmentService.getAllPunishments(pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PunishmentDTO>> getPunishmentsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(punishmentService.getPunishmentsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PunishmentDTO> getPunishmentById(@PathVariable Long id) {
        return ResponseEntity.ok(punishmentService.getPunishmentById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePunishment(@PathVariable Long id) {
        punishmentService.deletePunishment(id);
        return ResponseEntity.noContent().build();
    }
}
