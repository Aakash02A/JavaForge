package com.javaforge.refreshtoken.service;

import com.javaforge.refreshtoken.dto.*;
import com.javaforge.refreshtoken.exception.DuplicateEmailException;
import com.javaforge.refreshtoken.exception.InvalidRefreshTokenException;
import com.javaforge.refreshtoken.model.AppUser;
import com.javaforge.refreshtoken.model.RefreshToken;
import com.javaforge.refreshtoken.repository.AppUserRepository;
import com.javaforge.refreshtoken.repository.RefreshTokenRepository;
import com.javaforge.refreshtoken.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenExpirationMillis;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService,
                       RefreshTokenRepository refreshTokenRepository,
                       @Value("${security.refresh-token.expiration}") long refreshTokenExpirationMillis) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpirationMillis = refreshTokenExpirationMillis;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
        try {
            AppUser user = userRepository.saveAndFlush(
                    new AppUser(email, passwordEncoder.encode(request.password())));
            return UserResponse.from(user);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateEmailException();
        }
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password()));
        return issueTokenPair(authentication.getName());
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        String presentedToken = request.refreshToken().trim();
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash(presentedToken))
                .orElseThrow(InvalidRefreshTokenException::new);
        Instant now = Instant.now();
        if (stored.getRevokedAt() != null) {
            revokeFamily(stored.getFamilyId(), now);
            throw new InvalidRefreshTokenException();
        }
        if (!stored.isActive(now) || userRepository.findByEmail(stored.getUserEmail()).isEmpty()) {
            throw new InvalidRefreshTokenException();
        }

        String nextToken = generateOpaqueToken();
        stored.revoke(now, hash(nextToken));
        refreshTokenRepository.save(stored);
        return issueTokenPair(stored.getUserEmail(), stored.getFamilyId(), nextToken);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.findByTokenHash(hash(request.refreshToken().trim()))
                .ifPresent(token -> {
                    if (token.getRevokedAt() == null) {
                        token.revoke(Instant.now(), null);
                        refreshTokenRepository.save(token);
                    }
                });
    }

    private AuthResponse issueTokenPair(String email) {
        return issueTokenPair(email, UUID.randomUUID().toString(), generateOpaqueToken());
    }

    private AuthResponse issueTokenPair(String email, String familyId, String refreshToken) {
        var principal = new org.springframework.security.core.userdetails.User(email, "", List.of());
        refreshTokenRepository.save(new RefreshToken(hash(refreshToken), email, familyId,
                Instant.now(), Instant.now().plusMillis(refreshTokenExpirationMillis)));
        return new AuthResponse(jwtService.generateToken(principal), "Bearer",
                jwtService.getExpirationSeconds(), refreshToken, refreshTokenExpirationMillis / 1000);
    }

    private void revokeFamily(String familyId, Instant timestamp) {
        var familyTokens = refreshTokenRepository.findAllByFamilyId(familyId);
        familyTokens.stream()
                .filter(token -> token.getRevokedAt() == null)
                .forEach(token -> token.revoke(timestamp, null));
        refreshTokenRepository.saveAll(familyTokens);
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
