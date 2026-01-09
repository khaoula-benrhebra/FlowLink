package org.supplychain.supplychain.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'autorisation par rôle avec Keycloak OAuth2
 * Utilise @WithMockUser pour simuler l'authentification Keycloak
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void adminRole_CanAccessAllEndpoints() throws Exception {
        mockMvc.perform(get("/api/customers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = {"GESTIONNAIRE_COMMERCIAL"})
    void userRole_CanAccessAllowedEndpoints() throws Exception {
        mockMvc.perform(get("/api/customers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }
}
