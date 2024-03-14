package ru.Bogachev.fileHosting.web.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JwtRequest {

    @NotNull(message = "Username cannot be null.")
    private String username;

    @NotNull(message = "Password cannot be null.")
    private String password;

}
