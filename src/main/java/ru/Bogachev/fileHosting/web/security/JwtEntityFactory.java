package ru.Bogachev.fileHosting.web.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.Bogachev.fileHosting.domain.model.user.Role;
import ru.Bogachev.fileHosting.domain.model.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JwtEntityFactory {
    public static JwtEntity create(final User user) {
        return new JwtEntity(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                mapToGrantedAuthority(new ArrayList<>(user.getRoles()))
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthority(
            final List<Role> roles
    ) {
        return roles.stream()
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
