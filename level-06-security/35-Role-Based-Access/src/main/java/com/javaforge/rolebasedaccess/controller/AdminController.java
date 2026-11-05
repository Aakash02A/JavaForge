package com.javaforge.rolebasedaccess.controller;

import com.javaforge.rolebasedaccess.dto.RoleUpdateRequest;
import com.javaforge.rolebasedaccess.dto.UserResponse;
import com.javaforge.rolebasedaccess.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public AdminDashboard dashboard() {
        return new AdminDashboard("Administrative dashboard");
    }

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return adminService.listUsers();
    }

    @PutMapping("/users/{userId}/role")
    public UserResponse updateRole(@PathVariable Long userId,
                                   @Valid @RequestBody RoleUpdateRequest request) {
        return adminService.updateRole(userId, request);
    }

    public record AdminDashboard(String message) {
    }
}
