package org.supplychain.supplychain.controller.Livraison;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.supplychain.supplychain.dto.Livraison.CustomerDTO;
import org.supplychain.supplychain.model.Customer;
import org.supplychain.supplychain.repository.Livraison.CustomerRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerDTO customerDTO;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        customerRepository.deleteAll();

        // Create test customer DTO
        customerDTO = new CustomerDTO();
        customerDTO.setName("Test Customer");
        customerDTO.setEmail("test@example.com");
        customerDTO.setPhone("33612345678"); // Valid phone: 11 digits
        customerDTO.setAddress("123 Test Street");

        // Create test customer entity
        customer = new Customer();
        customer.setName("Existing Customer");
        customer.setEmail("existing@example.com");
        customer.setPhone("33787654321"); // Valid phone: 11 digits
        customer.setAddress("456 Existing Street");
    }

    @Test
    void createCustomer_Success() throws Exception {
        // Given: un nouveau client avec données valides
        // When: on envoie une requête POST pour créer le client
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                // Then: la réponse doit avoir le code 201 CREATED
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Client créé avec succès"))
                .andExpect(jsonPath("$.data.name").value("Test Customer"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        // Verify customer was saved in database
        assert customerRepository.count() == 1;
    }

    @Test
    void createCustomer_InvalidData() throws Exception {
        // Given: un client avec données invalides (nom vide)
        customerDTO.setName("");

        // When: on envoie une requête POST
        // Then: la réponse doit avoir le code 400 BAD_REQUEST
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest());

        // Verify customer was not saved
        assert customerRepository.count() == 0;
    }

    @Test
    void updateCustomer_Success() throws Exception {
        // Given: un client existant en base de données
        Customer savedCustomer = customerRepository.save(customer);
        Long customerId = savedCustomer.getIdCustomer();

        CustomerDTO updateDTO = new CustomerDTO();
        updateDTO.setName("Updated Customer");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setPhone("33612345999"); // Valid phone: 11 digits
        updateDTO.setAddress("789 Updated Street");

        // When: on envoie une requête PUT pour modifier le client
        mockMvc.perform(put("/api/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir les nouvelles données
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Client modifié avec succès"))
                .andExpect(jsonPath("$.data.name").value("Updated Customer"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"));

        // Verify customer was updated in database
        Customer updatedCustomer = customerRepository.findById(customerId).orElseThrow();
        assert updatedCustomer.getName().equals("Updated Customer");
        assert updatedCustomer.getEmail().equals("updated@example.com");
    }

    @Test
    void updateCustomer_NotFound() throws Exception {
        // Given: un ID de client qui n'existe pas
        Long nonExistentId = 999L;

        CustomerDTO updateDTO = new CustomerDTO();
        updateDTO.setName("Updated Customer");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setPhone("33612345999"); // Valid phone: 11 digits
        updateDTO.setAddress("789 Updated Street");

        // When: on envoie une requête PUT
        // Then: la réponse doit avoir le code 404 NOT_FOUND
        mockMvc.perform(put("/api/customers/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCustomer_Success() throws Exception {
        // Given: un client existant en base de données
        Customer savedCustomer = customerRepository.save(customer);
        Long customerId = savedCustomer.getIdCustomer();

        // When: on envoie une requête DELETE
        mockMvc.perform(delete("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                // Then: la réponse doit confirmer la suppression
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Client supprimé avec succès"));

        // Verify customer was deleted from database
        assert customerRepository.count() == 0;
    }

    @Test
    void deleteCustomer_NotFound() throws Exception {
        // Given: un ID de client qui n'existe pas
        Long nonExistentId = 999L;

        // When: on envoie une requête DELETE
        // Then: la réponse doit avoir le code 404 NOT_FOUND
        mockMvc.perform(delete("/api/customers/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCustomerById_Success() throws Exception {
        // Given: un client existant en base de données
        Customer savedCustomer = customerRepository.save(customer);
        Long customerId = savedCustomer.getIdCustomer();

        // When: on envoie une requête GET pour récupérer le client
        mockMvc.perform(get("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir les données du client
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("Existing Customer"))
                .andExpect(jsonPath("$.data.email").value("existing@example.com"));
    }

    @Test
    void getCustomerById_NotFound() throws Exception {
        // Given: un ID de client qui n'existe pas
        Long nonExistentId = 999L;

        // When: on envoie une requête GET
        // Then: la réponse doit avoir le code 404 NOT_FOUND
        mockMvc.perform(get("/api/customers/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllCustomers_Success() throws Exception {
        // Given: plusieurs clients en base de données
        customerRepository.save(customer);
        customerRepository.save(customer);

        // When: on envoie une requête GET pour récupérer tous les clients
        mockMvc.perform(get("/api/customers")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("direction", "asc"))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir la liste des clients
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test
    void getAllCustomers_EmptyList() throws Exception {
        // Given: aucun client en base de données

        // When: on envoie une requête GET
        mockMvc.perform(get("/api/customers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir une liste vide
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void searchCustomersByName_Success() throws Exception {
        // Given: des clients en base de données
        customerRepository.save(customer);
        customerRepository.save(customer);

        // When: on envoie une requête GET pour rechercher par nom
        mockMvc.perform(get("/api/customers/search")
                .param("name", "Existing")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir les clients correspondants
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void searchCustomersByName_NoResults() throws Exception {
        // Given: des clients en base de données
        customerRepository.save(customer);

        // When: on envoie une requête GET avec un nom qui ne correspond à rien
        mockMvc.perform(get("/api/customers/search")
                .param("name", "NonExistent")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir une liste vide
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test
    void getAllCustomers_WithPagination() throws Exception {
        // Given: plusieurs clients en base de données (plus que la taille de page)
        for (int i = 0; i < 15; i++) {
            Customer c = new Customer();
            c.setName("Customer " + i);
            c.setEmail("customer" + i + "@example.com");
            c.setPhone("3360000000" + String.format("%02d", i)); // Valid phone: 12 digits
            c.setAddress("Street " + i);
            customerRepository.save(c);
        }

        // When: on envoie une requête GET avec pagination
        mockMvc.perform(get("/api/customers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir exactement 10 clients
                .andExpect(jsonPath("$.data.content", hasSize(10)))
                .andExpect(jsonPath("$.data.totalElements").value(15))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.number").value(0));

        // When: on récupère la deuxième page
        mockMvc.perform(get("/api/customers")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                // Then: la réponse doit contenir 5 clients (15 - 10)
                .andExpect(jsonPath("$.data.content", hasSize(5)))
                .andExpect(jsonPath("$.data.number").value(1));
    }

    @Test
    void createCustomer_DuplicateEmail() throws Exception {
        // Given: un client existant en base de données
        customerRepository.save(customer);

        // Create a DTO with the same email
        CustomerDTO duplicateDTO = new CustomerDTO();
        duplicateDTO.setName("Another Customer");
        duplicateDTO.setEmail("existing@example.com"); // Same email
        duplicateDTO.setPhone("33611111111"); // Valid phone: 11 digits
        duplicateDTO.setAddress("Another Street");

        // When: on envoie une requête POST avec un email en doublon
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateDTO)))
                // Then: la requête doit échouer avec 409 Conflict
                .andExpect(status().isConflict());
    }
}
