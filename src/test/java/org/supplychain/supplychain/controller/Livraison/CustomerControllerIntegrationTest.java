package org.supplychain.supplychain.controller.Livraison;

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
        customerRepository.deleteAll();

        customerDTO = new CustomerDTO();
        customerDTO.setName("Test Customer");
        customerDTO.setEmail("test@example.com");
        customerDTO.setPhone("33612345678");
        customerDTO.setAddress("123 Test Street");

        customer = new Customer();
        customer.setName("Existing Customer");
        customer.setEmail("existing@example.com");
        customer.setPhone("33787654321");
        customer.setAddress("456 Existing Street");
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void createCustomer_Success() throws Exception {
        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Client créé avec succès"))
                .andExpect(jsonPath("$.data.name").value("Test Customer"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));

        assert customerRepository.count() == 1;
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void createCustomer_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void createCustomer_InvalidData() throws Exception {
        customerDTO.setName("");

        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isBadRequest());

        assert customerRepository.count() == 0;
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void updateCustomer_Success() throws Exception {
        Customer savedCustomer = customerRepository.save(customer);
        Long customerId = savedCustomer.getIdCustomer();

        CustomerDTO updateDTO = new CustomerDTO();
        updateDTO.setName("Updated Customer");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setPhone("33612345999");
        updateDTO.setAddress("789 Updated Street");

        mockMvc.perform(put("/api/customers/{id}", customerId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Client modifié avec succès"))
                .andExpect(jsonPath("$.data.name").value("Updated Customer"))
                .andExpect(jsonPath("$.data.email").value("updated@example.com"));

        Customer updatedCustomer = customerRepository.findById(customerId).orElseThrow();
        assert updatedCustomer.getName().equals("Updated Customer");
        assert updatedCustomer.getEmail().equals("updated@example.com");
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void updateCustomer_NotFound() throws Exception {
        Long nonExistentId = 999L;

        CustomerDTO updateDTO = new CustomerDTO();
        updateDTO.setName("Updated Customer");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setPhone("33612345999");
        updateDTO.setAddress("789 Updated Street");

        mockMvc.perform(put("/api/customers/{id}", nonExistentId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void deleteCustomer_Success() throws Exception {
        Customer savedCustomer = customerRepository.save(customer);
        Long customerId = savedCustomer.getIdCustomer();

        mockMvc.perform(delete("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Client supprimé avec succès"));

        assert customerRepository.count() == 0;
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void deleteCustomer_NotFound() throws Exception {
        Long nonExistentId = 999L;

        mockMvc.perform(delete("/api/customers/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void getCustomerById_Success() throws Exception {
        Customer savedCustomer = customerRepository.save(customer);
        Long customerId = savedCustomer.getIdCustomer();

        mockMvc.perform(get("/api/customers/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("Existing Customer"))
                .andExpect(jsonPath("$.data.email").value("existing@example.com"));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void getCustomerById_NotFound() throws Exception {
        Long nonExistentId = 999L;

        mockMvc.perform(get("/api/customers/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void getAllCustomers_Success() throws Exception {
        customerRepository.save(customer);
        customerRepository.save(customer);

        mockMvc.perform(get("/api/customers").param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("direction", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void getAllCustomers_EmptyList() throws Exception {
        mockMvc.perform(get("/api/customers").param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void searchCustomersByName_Success() throws Exception {
        customerRepository.save(customer);
        customerRepository.save(customer);

        mockMvc.perform(get("/api/customers/search").param("name", "Existing")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void searchCustomersByName_NoResults() throws Exception {
        customerRepository.save(customer);

        mockMvc.perform(get("/api/customers/search").param("name", "NonExistent")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content", hasSize(0)));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void getAllCustomers_WithPagination() throws Exception {
        for (int i = 0; i < 15; i++) {
            Customer c = new Customer();
            c.setName("Customer " + i);
            c.setEmail("customer" + i + "@example.com");
            c.setPhone("3360000000" + String.format("%02d", i));
            c.setAddress("Street " + i);
            customerRepository.save(c);
        }

        mockMvc.perform(get("/api/customers").param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(10)))
                .andExpect(jsonPath("$.data.totalElements").value(15))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.number").value(0));

        mockMvc.perform(get("/api/customers").param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(5)))
                .andExpect(jsonPath("$.data.number").value(1));
    }

    @Test

    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})    void createCustomer_DuplicateEmail() throws Exception {
        customerRepository.save(customer);

        CustomerDTO duplicateDTO = new CustomerDTO();
        duplicateDTO.setName("Another Customer");
        duplicateDTO.setEmail("existing@example.com");
        duplicateDTO.setPhone("33611111111");
        duplicateDTO.setAddress("Another Street");

        mockMvc.perform(post("/api/customers").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateDTO)))
                .andExpect(status().isConflict());
    }
}
