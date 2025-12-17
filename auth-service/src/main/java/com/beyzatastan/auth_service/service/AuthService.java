package com.beyzatastan.auth_service.service;

public interface AuthService {
    String signup(String username, String email, String rawPassword);
    String signin(String usernameOrEmail, String rawPassword);

    String refreshAccessToken(String refreshToken);

    void requestPasswordReset(String email);
    void resetPassword(String resetToken, String newPassword);
}
