package ru.Bogachev.fileHosting.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Auth Controller", description = "Auth API")
public class AuthController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final AuthService authService;

    @Operation(summary = "User Login")
    @PostMapping(value = "/login")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = JwtResponse.class)
            )
    )
    public ResponseEntity<JwtResponse> login(
            @RequestBody
            @Validated final JwtRequest jwtRequest
    ) {
        return ResponseEntity.ok(authService.login(jwtRequest));
    }

    @Operation(summary = "We receive updated tokens")
    @PostMapping(value = "/refresh")
    @ApiResponse(
            responseCode = "200",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = JwtResponse.class)
            )
    )
    public ResponseEntity<JwtResponse> refresh(
            @RequestBody
            @Validated final JwtRefresh request

    ) {
        return ResponseEntity.ok(authService.refresh(
                request.getRefreshToken())
        );
    }

    @Operation(summary = "User registration")
    @PostMapping(value = "/registration")
    @ApiResponse(
            responseCode = "201",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDto.class)
            )
    )
    public ResponseEntity<UserDto> registration(
            @RequestBody
            @Validated(OnCreate.class) final UserDto dto
    ) {
        User user = userMapper.toEntity(dto);
        User createdUser = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toDto(createdUser));
    }
}
