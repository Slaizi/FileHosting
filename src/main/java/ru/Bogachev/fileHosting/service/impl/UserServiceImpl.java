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

import java.util.List;
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
        checkIfUserExistsOrThrow(user);

        if (passwordValid(user)) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setRoles(Set.of(Role.ROLE_USER));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(final User user) {
        User userFromDb = getById(user.getId());
        if (!userFromDb.getUsername().equals(user.getUsername())) {
            checkIfUserExistsOrThrow(user);
            userFromDb.setUsername(user.getUsername());
        }
        if (passwordValid(user)) {
            userFromDb.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(userFromDb);
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
    public List<User> getUsers() {
        return userRepository.findAll();
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

    private void checkIfUserExistsOrThrow(final User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalStateException(
                    "User '%s' already exists.".formatted(user.getUsername())
            );
        }
    }

    private boolean passwordValid(final User user) {
        if (!user.getPassword().equals(user.getPasswordConformation())) {
            throw new IllegalStateException(
                    "Password and password confirmation do not match."
            );
        }
        return true;
    }
}
