package ru.Bogachev.fileHosting.web.security.expression;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.Bogachev.fileHosting.service.UserService;
import ru.Bogachev.fileHosting.web.security.JwtEntity;

@Service("securityExpression")
@RequiredArgsConstructor
public class SecurityExpression {
    private final UserService userService;

    public boolean canAccessUserFromFile(
            final String serverName
    ) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        JwtEntity entity = (JwtEntity) authentication.getPrincipal();

        return userService.isFileOwner(entity.getId(), serverName);
    }
}
