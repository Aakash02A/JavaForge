package com.javaforge.oauthlogin.controller;

import com.javaforge.oauthlogin.dto.ProfileResponse;
import com.javaforge.oauthlogin.model.OAuthAccount;
import com.javaforge.oauthlogin.repository.OAuthAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final OAuthAccountRepository accountRepository;

    public ProfileController(OAuthAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public ProfileResponse profile(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken oauthAuthentication)
                || !(oauthAuthentication.getPrincipal() instanceof OAuth2User oauthUser)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "OAuth authentication is required");
        }

        String provider = oauthAuthentication.getAuthorizedClientRegistrationId();
        String subject = oauthUser.getAttribute("sub");
        if (subject == null || subject.isBlank()) {
            subject = oauthUser.getName();
        }
        OAuthAccount account = accountRepository
                .findByProviderAndProviderSubject(provider, subject)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "OAuth account not found"));
        return ProfileResponse.from(account);
    }
}
