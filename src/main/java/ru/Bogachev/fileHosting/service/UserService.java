package ru.Bogachev.fileHosting.service;

import ru.Bogachev.fileHosting.domain.model.User;

public interface UserService {

    User create(User user);

    User getByUsername(String username);
}