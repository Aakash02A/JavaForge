package com.javaforge.rolebasedaccess.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/dashboard")
    public MessageResponse dashboard(Authentication authentication) {
        return new MessageResponse("User dashboard", authentication.getName());
    }

    public record MessageResponse(String message, String email) {
    }
}
