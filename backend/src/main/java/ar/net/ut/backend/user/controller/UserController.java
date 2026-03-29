package ar.net.ut.backend.user.controller;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.user.dto.activity.UserActivityDTO;
import ar.net.ut.backend.user.service.UserActivityService;
import ar.net.ut.backend.user.service.UserService;
import ar.net.ut.backend.user.dto.UserCreateDTO;
import ar.net.ut.backend.user.dto.UserDTO;
import ar.net.ut.backend.user.dto.UserUpdateDTO;
import ar.net.ut.backend.user.dto.profile.UserProfileDTO;
import ar.net.ut.backend.user.dto.profile.UserProfilePictureResponseDTO;
import ar.net.ut.backend.user.dto.profile.UserProfileUpdateDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(Global.API_VERSION_PATH + "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserActivityService userActivityService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @RequestParam(name = "referredBy", required = false) Long referredBy,
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

    @PatchMapping(value = "/profile/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfilePictureResponseDTO> updateUserProfilePicture(@RequestPart("pictureFile") MultipartFile pictureFile) {
        return ResponseEntity.ok(userService.updateUserProfilePicture(pictureFile));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileDTO> updateUserProfile(@RequestBody @Valid UserProfileUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateUserProfile(dto));
    }

    @PostMapping("/recent-activity")
    public ResponseEntity<Void> addUserRecentActivity(@RequestBody @Valid UserActivityDTO dto) {
        userActivityService.addUserRecentActivity(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<UserActivityDTO>> getUserRecentActivity() {
        return ResponseEntity.ok(userActivityService.getUserRecentActivity());
    }
}
