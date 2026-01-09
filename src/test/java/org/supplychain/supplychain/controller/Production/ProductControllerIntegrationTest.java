package org.supplychain.supplychain.controller.Production;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.supplychain.supplychain.dto.Production.ProductDTO;
import org.supplychain.supplychain.model.Product;
import org.supplychain.supplychain.repository.Production.ProductRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        product = new Product();
        product.setName("Existing Product");
        product.setDescription("An existing product");
        product.setCost(new java.math.BigDecimal("49.99"));
        product.setStock(50);
        product.setMinimumStock(5);
        product.setUnit("unit");
        product.setProductionTime(3);
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void getAllProducts_Success() throws Exception {
        productRepository.save(product);
        productRepository.save(product);

        mockMvc.perform(get("/api/products").param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Liste des produits récupérée avec succès"))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void getAllProducts_EmptyList() throws Exception {
        mockMvc.perform(get("/api/products").param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void getProductById_Success() throws Exception {
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getIdProduct();

        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("Existing Product"));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void getProductById_NotFound() throws Exception {
        Long nonExistentId = 999L;

        mockMvc.perform(get("/api/products/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void deleteProduct_Success() throws Exception {
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getIdProduct();

        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Produit supprimé avec succès"));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void deleteProduct_NotFound() throws Exception {
        Long nonExistentId = 999L;

        mockMvc.perform(delete("/api/products/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void searchProductsByName_Success() throws Exception {
        productRepository.save(product);

        mockMvc.perform(get("/api/products/search").param("name", "Existing")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void searchProductsByName_NoResults() throws Exception {
        productRepository.save(product);

        mockMvc.perform(get("/api/products/search").param("name", "NonExistent")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void getAllProducts_WithPagination() throws Exception {
        for (int i = 0; i < 15; i++) {
            Product p = new Product();
            p.setName("Product " + i);
            p.setDescription("Description " + i);
            p.setCost(new java.math.BigDecimal("100.00").add(java.math.BigDecimal.valueOf(i)));
            p.setStock(10);
            p.setMinimumStock(2);
            p.setUnit("unit");
            p.setProductionTime(2);
            productRepository.save(p);
        }

        mockMvc.perform(get("/api/products").param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(10)))
                .andExpect(jsonPath("$.data.totalElements").value(15));
    }
}
