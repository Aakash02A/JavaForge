package com.javaforge.rolebasedaccess.dto;

import com.javaforge.rolebasedaccess.model.AppUser;
import com.javaforge.rolebasedaccess.model.Role;

import java.time.Instant;

public record UserResponse(Long id, String email, Role role, Instant createdAt) {

    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
