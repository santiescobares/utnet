package ar.net.ut.backend.user;

import ar.net.ut.backend.auth.token.TokenException;
import ar.net.ut.backend.auth.token.TokenService;
import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.enums.ResourceType;
import ar.net.ut.backend.exception.impl.ResourceNotFoundException;
import ar.net.ut.backend.user.dto.UserCreateDTO;
import ar.net.ut.backend.user.dto.UserDTO;
import ar.net.ut.backend.user.dto.UserUpdateDTO;
import ar.net.ut.backend.user.entity.User;
import ar.net.ut.backend.user.enums.Role;
import ar.net.ut.backend.user.event.UserCreateEvent;
import ar.net.ut.backend.user.event.UserDeleteEvent;
import ar.net.ut.backend.user.event.UserUpdateEvent;
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

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static ar.net.ut.backend.Global.RedisKeys.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TokenService tokenService;

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final StringRedisTemplate redisTemplate;

    private final ApplicationEventPublisher eventPublisher;

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
