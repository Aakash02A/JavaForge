package com.javaforge.oauthlogin.service;

import com.javaforge.oauthlogin.model.OAuthAccount;
import com.javaforge.oauthlogin.repository.OAuthAccountRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class OAuthAccountService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuthAccountRepository accountRepository;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    public OAuthAccountService(OAuthAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = delegate.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        upsertLocalAccount(provider, oauthUser);
        return oauthUser;
    }

    @Transactional
    public OAuthAccount upsertLocalAccount(String provider, OAuth2User oauthUser) {
        String subject = oauthUser.getName();
        String email = attribute(oauthUser, "email", subject + "@" + provider + ".local")
                .trim().toLowerCase(Locale.ROOT);
        String displayName = attribute(oauthUser, "name", email);

        return accountRepository.findByProviderAndProviderSubject(provider, subject)
                .map(account -> {
                    account.updateProfile(email, displayName);
                    return accountRepository.save(account);
                })
                .orElseGet(() -> accountRepository.save(new OAuthAccount(provider, subject, email, displayName)));
    }

    private String attribute(OAuth2User user, String name, String fallback) {
        Object value = user.getAttributes().get(name);
        return value == null || value.toString().isBlank() ? fallback : value.toString();
    }
}
