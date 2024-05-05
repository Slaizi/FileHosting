package ru.Bogachev.fileHosting.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.Bogachev.fileHosting.domain.exception.AccessDeniedException;
import ru.Bogachev.fileHosting.domain.model.user.User;
import ru.Bogachev.fileHosting.service.UserService;
import ru.Bogachev.fileHosting.web.dto.user.UserDto;
import ru.Bogachev.fileHosting.web.dto.validation.OnUpdate;
import ru.Bogachev.fileHosting.web.mappers.UserMapper;
import ru.Bogachev.fileHosting.web.security.JwtEntity;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(value = "/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User Controller",
        description = "Controller for working with users")
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;

    @Operation(summary = "Get user by id")
    @GetMapping(value = "/{id}")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDto.class)
            )
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable(name = "id")
            @Parameter(
                    description = "User id",
                    required = true
            ) final String id
    ) {
        if (id.isBlank()) {
            throw new IllegalArgumentException(
                    "User id cannot be empty."
            );
        }
        UUID userId;
        try {
            userId = UUID.fromString(id.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Id with '%s' is not correct.".formatted(id)
            );
        }
        User user = userService.getById(userId);
        return ResponseEntity.ok(userMapper.toDto(user));
    }


    @Operation(summary = "Get list of users")
    @GetMapping(value = "/all")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = UserDto.class)
                    )
            )
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.getUsers();
        return ResponseEntity.ok(userMapper.toDto(users));
    }

    @Operation(summary = "Update user")
    @PutMapping(value = "/update")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserDto.class)
            )
    )
    public ResponseEntity<UserDto> update(
            @AuthenticationPrincipal final JwtEntity entity,
            @Validated(OnUpdate.class)
            @RequestBody final UserDto dto
    ) {
        if (!dto.getId().equals(entity.getId())) {
            throw new AccessDeniedException();
        }
        User user = userService.update(userMapper.toEntity(dto));
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
