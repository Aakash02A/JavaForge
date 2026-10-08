package com.javaforge.rolebasedaccess.config;

import com.javaforge.rolebasedaccess.model.AppUser;
import com.javaforge.rolebasedaccess.model.Role;
import com.javaforge.rolebasedaccess.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class BootstrapAdminRunner implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public BootstrapAdminRunner(AppUserRepository userRepository, PasswordEncoder passwordEncoder,
                                @Value("${app.bootstrap-admin.email}") String email,
                                @Value("${app.bootstrap-admin.password}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (email.isBlank() && password.isBlank()) {
            return;
        }
        if (email.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Set both BOOTSTRAP_ADMIN_EMAIL and BOOTSTRAP_ADMIN_PASSWORD");
        }
        if (password.length() < 12 || password.length() > 72) {
            throw new IllegalStateException("BOOTSTRAP_ADMIN_PASSWORD must contain between 12 and 72 characters");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        userRepository.findByEmail(normalizedEmail).ifPresentOrElse(user -> {
            if (user.getRole() != Role.ADMIN) {
                throw new IllegalStateException("Bootstrap account exists but is not an ADMIN");
            }
        }, () -> userRepository.saveAndFlush(
                new AppUser(normalizedEmail, passwordEncoder.encode(password), Role.ADMIN)));
    }
}
