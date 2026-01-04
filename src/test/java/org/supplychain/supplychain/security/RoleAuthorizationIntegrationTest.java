package org.supplychain.supplychain.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.repository.RefreshTokenRepository;
import org.supplychain.supplychain.repository.UserRepository;
import org.supplychain.supplychain.util.TestJwtUtil;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    void adminRole_CanAccessAllEndpoints() throws Exception {
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void userRole_CanAccessAllowedEndpoints() throws Exception {
        mockMvc.perform(get("/api/customers")
                .header("Authorization", "Bearer " + userToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + userToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }
}
