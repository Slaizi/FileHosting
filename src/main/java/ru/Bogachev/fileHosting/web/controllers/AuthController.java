package ru.Bogachev.fileHosting.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.Bogachev.fileHosting.domain.model.user.User;
import ru.Bogachev.fileHosting.service.AuthService;
import ru.Bogachev.fileHosting.service.UserService;
import ru.Bogachev.fileHosting.web.dto.auth.JwtRefresh;
import ru.Bogachev.fileHosting.web.dto.auth.JwtRequest;
import ru.Bogachev.fileHosting.web.dto.auth.JwtResponse;
import ru.Bogachev.fileHosting.web.dto.user.UserDto;
import ru.Bogachev.fileHosting.web.dto.validation.OnCreate;
import ru.Bogachev.fileHosting.web.mappers.UserMapper;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final AuthService authService;

    @PostMapping(value = "/login")
    public JwtResponse login(
            @RequestBody
            @Validated final JwtRequest jwtRequest
    ) {
        return authService.login(jwtRequest);
    }

    @PostMapping(value = "/refresh")
    public JwtResponse refresh(
            @RequestBody final JwtRefresh request
    ) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping(value = "/register")
    public UserDto registration(
            @RequestBody
            @Validated(OnCreate.class) final UserDto dto
    ) {
        User user = userMapper.toEntity(dto);
        User createdUser = userService.create(user);
        return userMapper.toDto(createdUser);
    }
}
