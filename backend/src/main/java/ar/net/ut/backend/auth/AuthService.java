package ar.net.ut.backend.auth;

import ar.net.ut.backend.auth.dto.LoginRequestDTO;
import ar.net.ut.backend.auth.dto.LoginResponseDTO;
import ar.net.ut.backend.auth.event.logout.PostLogoutEvent;
import ar.net.ut.backend.auth.event.login.PreLoginEvent;
import ar.net.ut.backend.auth.event.login.PostLoginEvent;
import ar.net.ut.backend.auth.event.logout.PreLogoutEvent;
import ar.net.ut.backend.auth.exception.BannedUserException;
import ar.net.ut.backend.auth.token.TokenException;
import ar.net.ut.backend.auth.token.TokenService;
import ar.net.ut.backend.config.JwtConfig;
import ar.net.ut.backend.exception.impl.ThirdPartyException;
import ar.net.ut.backend.user.mapper.UserMapper;
import ar.net.ut.backend.user.service.UserService;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.util.CookieUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static ar.net.ut.backend.Global.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final UserService userService;

    private final UserMapper userMapper;

    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    private final StringRedisTemplate redisTemplate;

    private final ApplicationEventPublisher eventPublisher;

    private final JwtConfig jwtConfig;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto, HttpServletRequest request, HttpServletResponse response) {
        eventPublisher.publishEvent(new PreLoginEvent(request, response));

        GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(dto.googleIdToken());
            if (idToken == null) {
                throw new TokenException("Invalid or expired Google idToken=" + dto.googleIdToken(), HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            log.error("An error ocurred while trying to login with Google. {}", e.getMessage());
            throw new ThirdPartyException("An error ocurred while trying to login with Google");
        }

        String incomingAccessToken = CookieUtil.getCookie(request, ACCESS_TOKEN_COOKIE);
        if (incomingAccessToken != null) {
            CookieUtil.clearHttpOnlyCookie(response, ACCESS_TOKEN_COOKIE);
            blacklistToken(incomingAccessToken);
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        Optional<User> userOp = userService.findByEmail(payload.getEmail());

        LoginResponseDTO responseDTO;
        if (userOp.isPresent()) {
            User user = userOp.get();
            CookieUtil.setHttpOnlyCookie(
                    response,
                    ACCESS_TOKEN_COOKIE,
                    tokenService.generateAccessToken(user),
                    jwtConfig.getAccessExpiration()
            );

            if (user.isBanned()) {
                throw new BannedUserException("User with id=" + user.getId() + " is banned");
            }

            responseDTO = new LoginResponseDTO(null, userMapper.toDTO(user));

            eventPublisher.publishEvent(new PostLoginEvent(request, response, user));
        } else {
            responseDTO = new LoginResponseDTO(tokenService.generateRegistrationToken(payload), null);
        }

        return responseDTO;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        eventPublisher.publishEvent(new PreLogoutEvent(request, response));

        clearAccessToken(request, response);

        eventPublisher.publishEvent(new PostLogoutEvent(request, response));
    }

    public void clearAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String incomingToken = CookieUtil.getCookie(request, ACCESS_TOKEN_COOKIE);
        if (incomingToken != null) {
            blacklistToken(incomingToken);
        }
        CookieUtil.clearHttpOnlyCookie(response, ACCESS_TOKEN_COOKIE);
    }

    private void blacklistToken(String rawToken) {
        DecodedJWT token = tokenService.decodeToken(rawToken);
        if (token == null) return;

        redisTemplate.opsForValue().set(
                RedisKeys.TOKEN_BLACKLIST + token.getId(),
                Instant.now().toString(),
                token.getExpiresAt().getTime() - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS
        );
    }
}
