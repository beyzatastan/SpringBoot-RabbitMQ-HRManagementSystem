package com.beyzatastan.auth_service.service;

import com.beyzatastan.auth_service.entity.RefreshToken;
import com.beyzatastan.auth_service.entity.User;

public interface RefreshTokenService {
    RefreshToken create(User user);
    RefreshToken verify(String token);
    void revokeAll(User user);
}
