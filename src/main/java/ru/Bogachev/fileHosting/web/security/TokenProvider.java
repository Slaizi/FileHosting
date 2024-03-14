package ru.Bogachev.fileHosting.web.security;

import org.springframework.security.core.Authentication;
import ru.Bogachev.fileHosting.domain.model.user.User;
import ru.Bogachev.fileHosting.web.dto.auth.JwtResponse;

public interface TokenProvider {
    String createAccessToken(User user);

    String createRefreshToken(User user);

    JwtResponse refreshUserTokens(String refreshToken);

    Boolean validateToken(String token);

    Authentication getAuthentication(String token);

}
