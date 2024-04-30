package ru.Bogachev.fileHosting.web.dto.auth;

import java.util.UUID;


public record JwtResponse(UUID id, String username,
                          String accessToken, String refreshToken) {
}
