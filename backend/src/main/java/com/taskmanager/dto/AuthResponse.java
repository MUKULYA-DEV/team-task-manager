package com.taskmanager.dto;

import com.taskmanager.models.Role;

public record AuthResponse(
        String token, String tokenType, String email, String role) {

    public static AuthResponse of(String token, String email, Role role) {
        return new AuthResponse(token, "Bearer", email, role.name());
    }
}
