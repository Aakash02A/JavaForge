package com.javaforge.rolebasedaccess.service;

import com.javaforge.rolebasedaccess.dto.AuthResponse;
import com.javaforge.rolebasedaccess.dto.LoginRequest;
import com.javaforge.rolebasedaccess.dto.RegisterRequest;
import com.javaforge.rolebasedaccess.dto.UserResponse;
import com.javaforge.rolebasedaccess.exception.DuplicateEmailException;
import com.javaforge.rolebasedaccess.model.AppUser;
import com.javaforge.rolebasedaccess.model.Role;
import com.javaforge.rolebasedaccess.repository.AppUserRepository;
import com.javaforge.rolebasedaccess.security.JwtService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
        try {
            AppUser user = userRepository.saveAndFlush(
                    new AppUser(email, passwordEncoder.encode(request.password()), Role.USER));
            return UserResponse.from(user);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException();
        }
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));
        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());
        return new AuthResponse(token, "Bearer", jwtService.getExpirationSeconds());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
