package ru.Bogachev.fileHosting.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Schema(description = "Jwt response")
public class JwtResponse {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;


    @Schema(
            description = "username",
            example = "John",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String username;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String accessToken;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String refreshToken;

}
