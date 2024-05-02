package ru.Bogachev.fileHosting.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Jwt refresh")
public class JwtRefresh {

    @Schema(
            description = "refresh token",
            example = "enter refresh token"
    )
    @NotBlank(message = "Refresh token cannot be empty.")
    private String refreshToken;

}
