package ru.Bogachev.fileHosting.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Jwt request")
public class JwtRequest {

    @Schema(
            description = "username",
            example = "John"
    )
    @NotBlank(message = "Username cannot be null.")
    private String username;

    @Schema(
            description = "password",
            example = "1234"
    )
    @NotBlank(message = "Password cannot be null.")
    private String password;

}
