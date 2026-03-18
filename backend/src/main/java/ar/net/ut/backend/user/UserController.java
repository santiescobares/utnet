package ar.net.ut.backend.user;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.user.dto.UserCreateDTO;
import ar.net.ut.backend.user.dto.UserDTO;
import ar.net.ut.backend.user.dto.UserUpdateDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @RequestParam(required = false) Long referredBy,
            @RequestBody @Valid UserCreateDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(referredBy, dto));
    }

    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody @Valid UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateUser(dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(HttpServletRequest request, HttpServletResponse response) {
        userService.deleteUser(request, response);
        return ResponseEntity.noContent().build();
    }
}
