package ru.Bogachev.fileHosting.web.security;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.Bogachev.fileHosting.service.UserService;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    @SneakyThrows
    public UserDetails loadUserByUsername(
            final String username
    ) {
        return JwtEntityFactory.create(
                userService.getByUsername(username)
        );
    }
}
