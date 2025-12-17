package com.beyzatastan.auth_service.service;

import com.beyzatastan.auth_service.entity.User;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String extractUsername(String token);
    boolean isTokenValid(String token);
}
