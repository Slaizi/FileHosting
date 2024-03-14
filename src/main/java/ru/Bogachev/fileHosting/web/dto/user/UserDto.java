package ru.Bogachev.fileHosting.web.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.Bogachev.fileHosting.web.dto.validation.OnCreate;
import ru.Bogachev.fileHosting.web.dto.validation.OnUpdate;

import java.util.UUID;

@Data
public class UserDto {

    @NotNull(
            message = "Id must be not null.",
            groups = {OnUpdate.class}
    )
    private UUID id;

    @Length(
            max = 255,
            message = "Username length must be smaller than 255 symbols",
            groups = {OnCreate.class, OnUpdate.class}
    )
    @NotNull(
            message = "Username must be not null.",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(
            message = "Password must be not null",
            groups = {OnCreate.class, OnUpdate.class}
    )
    private String password;

    @NotNull(
            message = "Password confirmation must be not null.",
            groups = {OnCreate.class}
    )
    private String passwordConformation;

}
