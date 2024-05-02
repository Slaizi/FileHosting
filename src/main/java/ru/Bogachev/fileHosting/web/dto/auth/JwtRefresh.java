package ru.Bogachev.fileHosting.web.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JwtRefresh {

    @NotBlank(message = "Refresh token cannot be empty.")
    private String refreshToken;

}
