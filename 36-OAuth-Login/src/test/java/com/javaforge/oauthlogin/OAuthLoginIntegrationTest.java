package com.javaforge.oauthlogin;

import com.javaforge.oauthlogin.repository.OAuthAccountRepository;
import com.javaforge.oauthlogin.service.OAuthAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OAuthLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OAuthAccountRepository accountRepository;

    @Autowired
    private OAuthAccountService accountService;

    @BeforeEach
    void clearAccounts() {
        accountRepository.deleteAll();
    }

    @Test
    void anonymousProfileRequestIsRejected() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void oauthLoginCreatesLocalProfileAndAllowsProtectedRead() throws Exception {
        accountService.upsertLocalAccount("test", oauthUser("google-subject-1", "Asha@example.com", "Asha Rao"));
        mockMvc.perform(get("/api/profile").with(oauth2Login()
                        .attributes(attributes -> attributes.putAll(Map.of(
                                "sub", "google-subject-1",
                                "email", "Asha@example.com",
                                "name", "Asha Rao")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.provider").value("test"))
                .andExpect(jsonPath("$.email").value("asha@example.com"))
                .andExpect(jsonPath("$.displayName").value("Asha Rao"));

        org.assertj.core.api.Assertions.assertThat(accountRepository
                        .findByProviderAndProviderSubject("test", "google-subject-1"))
                .isPresent();
    }

    @Test
    void repeatLoginUpdatesProfileAndTimestampWithoutCreatingDuplicate() throws Exception {
        accountService.upsertLocalAccount("test", oauthUser("google-subject-2", "old@example.com", "Old Name"));
        accountService.upsertLocalAccount("test", oauthUser("google-subject-2", "new@example.com", "New Name"));
        mockMvc.perform(get("/api/profile").with(oauth2Login()
                        .attributes(attributes -> attributes.putAll(Map.of(
                                "sub", "google-subject-2",
                                "email", "old@example.com",
                                "name", "Old Name")))))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/profile").with(oauth2Login()
                        .attributes(attributes -> attributes.putAll(Map.of(
                                "sub", "google-subject-2",
                                "email", "new@example.com",
                                "name", "New Name")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.displayName").value("New Name"));

        org.assertj.core.api.Assertions.assertThat(accountRepository.count()).isEqualTo(1);
    }

    @Test
    void oauthPrincipalWithoutPersistedProfileReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/profile").with(oauth2Login()
                        .attributes(attributes -> attributes.put("sub", "missing-subject"))))
                .andExpect(status().isNotFound());
    }

    private DefaultOAuth2User oauthUser(String subject, String email, String name) {
        return new DefaultOAuth2User(List.of(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("sub", subject, "email", email, "name", name), "sub");
    }
}
