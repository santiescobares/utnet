package ar.net.ut.backend.config.security.jwt;

import ar.net.ut.backend.auth.token.TokenService;
import ar.net.ut.backend.config.JwtConfig;
import ar.net.ut.backend.context.RequestContextHolder;
import ar.net.ut.backend.util.CookieUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static ar.net.ut.backend.Global.*;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final StringRedisTemplate redisTemplate;

    private final JwtConfig jwtConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String rawToken = CookieUtil.getCookie(request, ACCESS_TOKEN_COOKIE);
        if (SecurityContextHolder.getContext().getAuthentication() != null || rawToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        DecodedJWT accessToken = tokenService.decodeToken(rawToken);
        if (accessToken == null || redisTemplate.hasKey(RedisKeys.TOKEN_BLACKLIST + accessToken.getId())) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = accessToken.getClaim("userId").asString();
        String logoutTimestamp = redisTemplate.opsForValue().get(RedisKeys.FORCED_LOGOUT + userId);

        if (logoutTimestamp != null && accessToken.getIssuedAtAsInstant().toEpochMilli() < Long.parseLong(logoutTimestamp)) {
            redisTemplate.opsForValue().set(
                    RedisKeys.TOKEN_BLACKLIST + accessToken.getId(),
                    Instant.now().toString(),
                    accessToken.getExpiresAt().getTime() - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
            );
            CookieUtil.clearHttpOnlyCookie(response, ACCESS_TOKEN_COOKIE);
        } else {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    accessToken.getSubject(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(accessToken.getClaim("role").asString()))
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
            RequestContextHolder.setCurrentUser(UUID.fromString(userId));
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestContextHolder.clear();
        }
    }
}
