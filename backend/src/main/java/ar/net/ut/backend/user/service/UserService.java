package ar.net.ut.backend.user.service;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.auth.token.TokenException;
import ar.net.ut.backend.auth.token.TokenService;
import ar.net.ut.backend.config.S3Config;
import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.service.StorageService;
import ar.net.ut.backend.user.mapper.UserMapper;
import ar.net.ut.backend.user.dto.UserCreateDTO;
import ar.net.ut.backend.user.dto.UserDTO;
import ar.net.ut.backend.user.dto.UserUpdateDTO;
import ar.net.ut.backend.user.dto.profile.UserProfileDTO;
import ar.net.ut.backend.user.dto.profile.UserProfilePictureResponseDTO;
import ar.net.ut.backend.user.dto.profile.UserProfileUpdateDTO;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.enums.Role;
import ar.net.ut.backend.user.event.UserCreateEvent;
import ar.net.ut.backend.user.event.UserDeleteEvent;
import ar.net.ut.backend.user.event.UserUpdateEvent;
import ar.net.ut.backend.user.repository.UserRepository;
import ar.net.ut.backend.util.ImageUtil;
import ar.net.ut.backend.util.RandomUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static ar.net.ut.backend.Global.RedisKeys.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TokenService tokenService;
    private final StorageService storageService;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final StringRedisTemplate redisTemplate;

    private final ApplicationEventPublisher eventPublisher;

    private final S3Config s3Config;

    @Transactional
    public UserDTO createUser(Long inviterReferralId, UserCreateDTO dto) {
        String rawToken = dto.registrationToken();
        DecodedJWT token = tokenService.decodeToken(rawToken);
        if (token == null || token.getClaim("type").as(TokenService.TokenType.class) != TokenService.TokenType.REGISTRATION) {
            throw new TokenException("Invalid or expired registrationToken=" + rawToken, HttpStatus.FORBIDDEN);
        }

        String tokenBlacklistKey = TOKEN_BLACKLIST + token.getId();
        if (redisTemplate.hasKey(tokenBlacklistKey)) {
            throw new TokenException("Invalid or expired registrationToken=" + rawToken, HttpStatus.FORBIDDEN);
        }

        User referredBy;
        if (inviterReferralId != null) {
            referredBy = userRepository.findByReferralId(inviterReferralId)
                    .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, "referralId", Long.toString(inviterReferralId)));
        } else {
            referredBy = null;
        }

        String email = token.getClaim("email").asString();
        User user = userRepository.findByEmailIncludingDeleted(email).orElse(new User());

        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setBirthday(dto.birthday());

        if (user.getDeletedAt() == null) {
            user.setEmail(token.getClaim("email").asString());
            user.setRole(Role.NEW_USER);
            user.setGoogleId(token.getSubject());
            user.setReferralId(RandomUtil.randomLongId(8));
            user.setReferredBy(referredBy);
        } else {
            user.setDeletedAt(null);
        }

        user.createProfile();

        userRepository.save(user);

        redisTemplate.opsForValue().set(
                tokenBlacklistKey,
                Instant.now().toString(),
                token.getExpiresAt().getTime() - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
        );

        eventPublisher.publishEvent(new UserCreateEvent(user));

        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO updateUser(UserUpdateDTO dto) {
        User user = getCurrentUser();
        userMapper.updateFromDTO(user, dto);
        userRepository.save(user);

        eventPublisher.publishEvent(new UserUpdateEvent(user));

        return userMapper.toDTO(user);
    }

    @Transactional
    public void deleteUser(HttpServletRequest request, HttpServletResponse response) {
        User user = getCurrentUser();
        userRepository.delete(user);

        eventPublisher.publishEvent(new UserDeleteEvent(user, request, response));
    }

    public UserProfilePictureResponseDTO updateUserProfilePicture(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File can't be null or empty");
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (!extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("jpg")) {
            throw new IllegalArgumentException("Profile picture must be PNG or JPG");
        }
        if (file.getSize() > 10_485_760) {
            throw new IllegalArgumentException("Profile picture size can't be greater than 10 MB");
        }

        MultipartFile resizedFile;
        try {
            resizedFile = ImageUtil.resize(file, 256, 256);
        } catch (IOException e) {
            throw new RuntimeException("An error ocurred while trying to resize an image");
        }

        String pictureKey = storageService.uploadFile(resizedFile, s3Config.getPublicBucket(), Global.R2.PROFILE_PICTURES_PATH.toString());

        User user = getCurrentUser();
        user.getProfile().setPictureKey(pictureKey);
        userRepository.save(user);

        return new UserProfilePictureResponseDTO(Global.R2.PUBLIC_URL + "/" + pictureKey);
    }

    @Transactional
    public UserProfileDTO updateUserProfile(UserProfileUpdateDTO dto) {
        User user = getCurrentUser();
        userMapper.updateProfileFromDTO(user.getProfile(), dto);
        userRepository.save(user);

        eventPublisher.publishEvent(new UserUpdateEvent(user));

        return userMapper.toProfileDTO(user.getProfile());
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.USER, "id", id.toString()));
    }

    public User getCurrentUser() {
        UUID currentUser = RequestContextHolder.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user is loaded in current context");
        }
        return getById(currentUser);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }
}
