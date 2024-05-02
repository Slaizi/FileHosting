package ru.Bogachev.fileHosting.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.Bogachev.fileHosting.domain.exception.UserNotFoundException;
import ru.Bogachev.fileHosting.domain.model.user.Role;
import ru.Bogachev.fileHosting.domain.model.user.User;
import ru.Bogachev.fileHosting.repository.UserRepository;
import ru.Bogachev.fileHosting.service.UserService;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User create(final User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException(
                    "User already exist."
            );
        }
        if (!user.getPassword().equals(user.getPasswordConformation())) {
            throw new IllegalStateException(
                    "Password and password confirmation do not match."
            );
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.ROLE_USER));
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(final UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with %s not found", id)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public User getByUsername(final String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User named %s not found.", username)
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFileOwner(final UUID userId,
                               final String serverName) {
        return userRepository.isFileOwner(userId.toString(), serverName);
    }
}
