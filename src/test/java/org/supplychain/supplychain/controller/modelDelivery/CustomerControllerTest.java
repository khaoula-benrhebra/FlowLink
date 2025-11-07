package org.supplychain.supplychain.controller.modelDelivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.supplychain.supplychain.dto.Livraison.CustomerDto;
import org.supplychain.supplychain.service.modelDelivery.interfaces.ICustomerService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCustomer_ShouldReturnCreated() throws Exception {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();

        CustomerDto dto = new CustomerDto();
        dto.setEmail("new@example.com");
        dto.setName("Test User");

        when(customerService.createCustomer(any(CustomerDto.class))).thenReturn(dto);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("new@example.com"));
    }
}
