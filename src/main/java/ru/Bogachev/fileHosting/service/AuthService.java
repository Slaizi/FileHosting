package ru.Bogachev.fileHosting.service;

import ru.Bogachev.fileHosting.web.dto.auth.JwtRequest;
import ru.Bogachev.fileHosting.web.dto.auth.JwtResponse;

public interface AuthService {
    JwtResponse login(JwtRequest request);
    JwtResponse refresh(String refreshToken);
}
