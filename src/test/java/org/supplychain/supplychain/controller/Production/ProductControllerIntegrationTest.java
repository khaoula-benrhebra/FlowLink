package org.supplychain.supplychain.controller.Production;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
        // Clear database before each test
        productRepository.deleteAll();

        // Create test product entity
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
    void getAllProducts_Success() throws Exception {
        // Given: plusieurs produits en base de données
        productRepository.save(product);
        productRepository.save(product);

        // When: on envoie une requête GET pour récupérer tous les produits
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir la liste des produits
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Liste des produits récupérée avec succès"))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test
    void getAllProducts_EmptyList() throws Exception {
        // Given: aucun produit en base de données

        // When: on envoie une requête GET
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir une liste vide
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void getProductById_Success() throws Exception {
        // Given: un produit existant en base de données
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getIdProduct();

        // When: on envoie une requête GET pour récupérer le produit
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir les données du produit
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("Existing Product"));
    }

    @Test
    void getProductById_NotFound() throws Exception {
        // Given: un ID de produit qui n'existe pas
        Long nonExistentId = 999L;

        // When: on envoie une requête GET
        // Then: la réponse doit avoir le code 404 NOT_FOUND
        mockMvc.perform(get("/api/products/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteProduct_Success() throws Exception {
        // Given: un produit existant en base de données
        Product savedProduct = productRepository.save(product);
        Long productId = savedProduct.getIdProduct();

        // When: on envoie une requête DELETE
        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isOk())
                // Then: la réponse doit confirmer la suppression
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Produit supprimé avec succès"));
    }

    @Test
    void deleteProduct_NotFound() throws Exception {
        // Given: un ID de produit qui n'existe pas
        Long nonExistentId = 999L;

        // When: on envoie une requête DELETE
        // Then: la réponse doit avoir le code 404 NOT_FOUND
        mockMvc.perform(delete("/api/products/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchProductsByName_Success() throws Exception {
        // Given: des produits en base de données
        productRepository.save(product);

        // When: on envoie une requête GET pour rechercher par nom
        mockMvc.perform(get("/api/products/search")
                .param("name", "Existing")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir les produits correspondants
                .andExpect(jsonPath("$.status").value(200));
    }

    @Test
    void searchProductsByName_NoResults() throws Exception {
        // Given: des produits en base de données
        productRepository.save(product);

        // When: on envoie une requête GET avec un nom qui ne correspond à rien
        mockMvc.perform(get("/api/products/search")
                .param("name", "NonExistent")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir une liste vide
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    void getAllProducts_WithPagination() throws Exception {
        // Given: plusieurs produits en base de données (plus que la taille de page)
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

        // When: on envoie une requête GET avec pagination
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir la bonne pagination
                .andExpect(jsonPath("$.data.content", hasSize(10)))
                .andExpect(jsonPath("$.data.totalElements").value(15));
    }
}
