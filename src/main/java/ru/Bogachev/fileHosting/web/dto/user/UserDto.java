package ru.Bogachev.fileHosting.web.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.Bogachev.fileHosting.web.dto.validation.OnCreate;
import ru.Bogachev.fileHosting.web.dto.validation.OnUpdate;

import java.util.UUID;

@Data
@Schema(description = "User DTO")
public class UserDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @NotNull(
            message = "Id must be not null.",
            groups = {OnUpdate.class}
    )
    private UUID id;

    @Schema(
            description = "username",
            example = "John"
    )
    @Length(
            max = 255,
            message = "Username length must be smaller than 255 symbols",
            groups = {OnCreate.class, OnUpdate.class}
    )
    @NotBlank(
            message = "Username must be not null.",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String username;

    @Schema(
            description = "password",
            example = "1234"
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(
            message = "Password must be not null",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String password;

    @Schema(
            description = "password conformation",
            example = "1234"
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(
            message = "Password confirmation must be not null.",
            groups = {OnCreate.class}
    )
    private String passwordConformation;

}
