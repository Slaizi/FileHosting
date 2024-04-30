package ru.Bogachev.fileHosting.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.Bogachev.fileHosting.domain.model.user.User;
import ru.Bogachev.fileHosting.service.AuthService;
import ru.Bogachev.fileHosting.service.UserService;
import ru.Bogachev.fileHosting.web.dto.auth.JwtRequest;
import ru.Bogachev.fileHosting.web.dto.auth.JwtResponse;
import ru.Bogachev.fileHosting.web.security.TokenProvider;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    @Override
    public JwtResponse login(final JwtRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userService.getByUsername(request.getUsername());
        return new JwtResponse(
                user.getId(),
                user.getUsername(),
                tokenProvider.createAccessToken(user),
                tokenProvider.createRefreshToken(user)
        );
    }

    @Override
    public JwtResponse refresh(final String refreshToken) {
        return tokenProvider.refreshUserTokens(refreshToken);
    }
}
