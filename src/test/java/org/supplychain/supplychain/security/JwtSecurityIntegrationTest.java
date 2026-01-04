package org.supplychain.supplychain.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.LoginRequest;
import org.supplychain.supplychain.repository.RefreshTokenRepository;
import org.supplychain.supplychain.repository.UserRepository;
import org.supplychain.supplychain.util.TestJwtUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JwtSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TestJwtUtil testJwtUtil;

    private String adminToken;
    private String userToken;

    @BeforeEach
    @Transactional
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        adminToken = testJwtUtil.generateAdminToken();
        userToken = testJwtUtil.generateUserToken();
    }

    // ========== AUTHENTIFICATION ==========
    
    @Test
    void login_ValidCredentials_ShouldReturnToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    void login_InvalidCredentials_ShouldReturn401() throws Exception {
        // Créer l'utilisateur d'abord
        testJwtUtil.createOrGetUser("admin@test.com", "admin123", org.supplychain.supplychain.enums.Role.ADMIN);
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    // ========== ACCÈS AVEC TOKEN ==========
    
    @Test
    void accessProtectedEndpoint_WithValidToken_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void accessProtectedEndpoint_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/customers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token JWT manquant"));
    }

    @Test
    void accessProtectedEndpoint_WithInvalidToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer invalid.token.here")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token JWT invalide ou expiré"));
    }

    @Test
    void accessProtectedEndpoint_WithExpiredToken_ShouldReturn401() throws Exception {
        String expiredToken = testJwtUtil.generateExpiredToken();
        
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer " + expiredToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token JWT invalide ou expiré"));
    }

    // ========== REFRESH TOKEN ==========
    
    @Test
    void refreshToken_WithValidRefreshToken_ShouldReturnNewToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("admin123");

        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String refreshToken = objectMapper.readTree(response).get("data").get("refreshToken").asText();

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void refreshToken_WithInvalidRefreshToken_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"invalid.refresh.token\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ========== ENDPOINTS PUBLICS ==========
    
    @Test
    void accessPublicEndpoint_WithoutToken_ShouldReturn200() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void accessSwaggerUI_WithoutToken_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void accessActuator_WithoutToken_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    // ========== VALIDATION STRICTE ==========
    
    @Test
    void allProtectedEndpoints_RequireToken() throws Exception {
        String[] protectedEndpoints = {
            "/api/customers",
            "/api/products",
            "/api/suppliers",
            "/api/raw-materials",
            "/api/supply-orders",
            "/api/production-orders",
            "/api/orders"
        };

        for (String endpoint : protectedEndpoints) {
            mockMvc.perform(get(endpoint)
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isUnauthorized());
        }
    }
}
