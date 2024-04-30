package ru.Bogachev.fileHosting.web.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.Bogachev.fileHosting.domain.exception.AccessDeniedException;
import ru.Bogachev.fileHosting.domain.model.user.Role;
import ru.Bogachev.fileHosting.domain.model.user.User;
import ru.Bogachev.fileHosting.service.UserService;
import ru.Bogachev.fileHosting.service.props.JwtProps;
import ru.Bogachev.fileHosting.web.dto.auth.JwtResponse;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {

    private final JwtProps jwtProps;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys
                .hmacShaKeyFor(
                        jwtProps.getSecret().getBytes()
                );
    }


    @Override
    public String createAccessToken(final User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("roles", resolverRoles(user.getRoles()));
        Instant validity = Instant.now()
                .plus(jwtProps.getAccess(), ChronoUnit.HOURS);

        return Jwts.builder()
                .subject(user.getUsername())
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    @Override
    public String createRefreshToken(final User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        Instant validity = Instant.now()
                .plus(jwtProps.getRefresh(), ChronoUnit.DAYS);

        return Jwts.builder()
                .subject(user.getUsername())
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    @Override
    public JwtResponse refreshUserTokens(final String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new AccessDeniedException();
        }
        UUID id = UUID.fromString(getIdFromToken(refreshToken));
        User user = userService.getById(id);
        return new JwtResponse(
                user.getId(),
                user.getUsername(),
                createAccessToken(user),
                createRefreshToken(user)
        );
    }

    @Override
    public boolean validateToken(final String token) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        return !claims.getPayload().getExpiration().before(new Date());
    }

    @Override
    public Authentication getAuthentication(
            final String token
    ) {
        String username = getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }

    private List<String> resolverRoles(final Set<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .toList();
    }

    private String getIdFromToken(final String token) {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        return claims.getPayload().get("id").toString();
    }

    private String getUsernameFromToken(final String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
