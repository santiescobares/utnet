package ar.net.ut.backend.user.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.user.service.UserInteractionService;
import ar.net.ut.backend.user.dto.interaction.UserInteractionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/users/interactions")
@RequiredArgsConstructor
public class UserInteractionController {

    private final UserInteractionService interactionService;

    @GetMapping
    public ResponseEntity<List<UserInteractionDTO>> getMyInteractions(
            @RequestParam(name = "resourceType") ResourceType resourceType,
            @RequestParam(name = "resourceId", required = false) String resourceId
    ) {
        return ResponseEntity.ok(interactionService.getMyInteractions(resourceType, resourceId));
    }
}
