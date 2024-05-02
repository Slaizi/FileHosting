package ru.Bogachev.fileHosting.web.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.Bogachev.fileHosting.service.UserService;
import ru.Bogachev.fileHosting.web.security.JwtEntity;

import java.util.UUID;

@Service("securityExpression")
@RequiredArgsConstructor
public class SecurityExpression {
    private final UserService userService;

    public boolean canAccessUserFromFile(
            final UUID userId,
            final String serverName
    ) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        JwtEntity entity = (JwtEntity) authentication.getPrincipal();

        return userId.equals(entity.getId()) && userService
                .isFileOwner(userId, serverName);
    }
}
