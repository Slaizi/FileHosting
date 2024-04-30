package ru.Bogachev.fileHosting.web.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtRequest {

    @NotBlank(message = "Username cannot be null.")
    private String username;

    @NotBlank(message = "Password cannot be null.")
    private String password;

}
