package ru.Bogachev.fileHosting.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.Bogachev.fileHosting.domain.model.user.User;
import ru.Bogachev.fileHosting.service.UserService;
import ru.Bogachev.fileHosting.web.dto.user.UserDto;
import ru.Bogachev.fileHosting.web.dto.validation.OnCreate;
import ru.Bogachev.fileHosting.web.mappers.UserMapper;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

    private final UserMapper userMapper;
    private final UserService userService;

    @PostMapping(value = "/register")
    public UserDto registration(
            @Validated(OnCreate.class)
            @RequestBody final UserDto dto
    ) {
        User user = userMapper.toEntity(dto);
        User createdUser = userService.create(user);
        return userMapper.toDto(createdUser);
    }
}
