package ru.Bogachev.fileHosting.service;

import ru.Bogachev.fileHosting.domain.model.user.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User create(User user);

    User update(User user);

    User getById(UUID id);

    List<User> getUsers();

    User getByUsername(String username);

    boolean isFileOwner(UUID userId, String serverName);
}
