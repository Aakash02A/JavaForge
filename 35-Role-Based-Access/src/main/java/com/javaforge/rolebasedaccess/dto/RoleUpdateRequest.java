package com.javaforge.rolebasedaccess.dto;

import com.javaforge.rolebasedaccess.model.Role;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(@NotNull Role role) {
}
