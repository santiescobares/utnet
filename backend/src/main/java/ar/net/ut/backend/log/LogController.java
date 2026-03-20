package ar.net.ut.backend.log;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.log.dto.LogCreateDTO;
import ar.net.ut.backend.log.dto.LogDTO;
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
@RequestMapping(Global.API_VERSION_PATH + "/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public ResponseEntity<LogDTO> createLog(@RequestBody @Valid LogCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(logService.createLog(dto));
    }

    @GetMapping
    public ResponseEntity<Page<LogDTO>> getAllLogs(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) ResourceType resourceType,
            @RequestParam(required = false) Log.Action action
    ) {
        return ResponseEntity.ok(logService.getAllLogs(pageable, resourceType, action));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogDTO> getLogById(@PathVariable Long id) {
        return ResponseEntity.ok(logService.getLogById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<LogDTO>> getLogsByUser(
            @PathVariable UUID userId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(logService.getLogsByUser(userId, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        logService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}
