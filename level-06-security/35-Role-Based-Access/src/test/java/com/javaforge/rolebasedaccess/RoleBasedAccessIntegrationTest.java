package com.javaforge.rolebasedaccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaforge.rolebasedaccess.model.AppUser;
import com.javaforge.rolebasedaccess.model.Role;
import com.javaforge.rolebasedaccess.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoleBasedAccessIntegrationTest {

    private static final String PASSWORD = "strong-password-123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clearUsers() {
        userRepository.deleteAll();
    }

    @Test
    void registrationAlwaysAssignsUserRoleAndDoesNotAcceptRequestedRole() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"new@example.com","password":"strong-password-123","role":"ADMIN"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void userCanAccessUserRouteButIsForbiddenFromAdminRoutes() throws Exception {
        register("user@example.com");
        String token = login("user@example.com");
        String bearer = "Bearer " + token;

        mockMvc.perform(get("/api/user/dashboard").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));

        mockMvc.perform(get("/api/admin/dashboard").header("Authorization", bearer))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You do not have permission to access this resource"));

        mockMvc.perform(get("/api/admin/users").header("Authorization", bearer))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanListAccountsAndChangeRoleAndNewRoleAppliesToExistingToken() throws Exception {
        register("admin@example.com");
        register("promoted@example.com");
        Long adminId = userRepository.findByEmail("admin@example.com").orElseThrow().getId();
        Long promotedId = userRepository.findByEmail("promoted@example.com").orElseThrow().getId();
        AppUser admin = userRepository.findById(adminId).orElseThrow();
        admin.changeRole(Role.ADMIN);
        userRepository.saveAndFlush(admin);

        String adminToken = login("admin@example.com");
        String promotedToken = login("promoted@example.com");

        mockMvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].passwordHash").doesNotExist());

        mockMvc.perform(put("/api/admin/users/{userId}/role", promotedId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role":"ADMIN"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        mockMvc.perform(get("/api/admin/dashboard").header("Authorization", "Bearer " + promotedToken))
                .andExpect(status().isOk());
    }

    @Test
    void unauthenticatedAndInvalidRoleRequestsAreRejected() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isUnauthorized());

        register("user@example.com");
        String userToken = login("user@example.com");
        Long userId = userRepository.findByEmail("user@example.com").orElseThrow().getId();

        mockMvc.perform(put("/api/admin/users/{userId}/role", userId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role":"SUPERUSER"}
                                """))
                .andExpect(status().isForbidden());

        String adminToken = createAdminAndLogin("admin@example.com");
        mockMvc.perform(put("/api/admin/users/{userId}/role", userId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role":"SUPERUSER"}
                                """))
                .andExpect(status().isBadRequest());
    }

    private void register(String email) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isCreated());
    }

    private String login(String email) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode json = objectMapper.readTree(response);
        return json.get("accessToken").asText();
    }

    private String createAdminAndLogin(String email) throws Exception {
        register(email);
        AppUser admin = userRepository.findByEmail(email).orElseThrow();
        admin.changeRole(Role.ADMIN);
        userRepository.saveAndFlush(admin);
        return login(email);
    }
}
