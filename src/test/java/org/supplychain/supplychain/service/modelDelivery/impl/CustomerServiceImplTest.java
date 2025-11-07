package org.supplychain.supplychain.service.modelDelivery.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.supplychain.supplychain.dto.Livraison.CustomerDto;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.mapper.modelDelivery.CustomerMapper;
import org.supplychain.supplychain.model.Customer;
import org.supplychain.supplychain.repository.modelDelivery.CustomerRepository;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomer_ShouldThrowDuplicateResourceException_WhenEmailExists() {
        CustomerDto dto = new CustomerDto();
        dto.setEmail("test@example.com");

        when(customerRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.createCustomer(dto));
    }

    @Test
    void createCustomer_ShouldReturnCustomerDto_WhenValid() {
        CustomerDto dto = new CustomerDto();
        dto.setEmail("new@example.com");

        Customer customerEntity = new Customer();
        CustomerDto savedDto = new CustomerDto();
        savedDto.setEmail("new@example.com");

        when(customerRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(customerMapper.toEntity(dto)).thenReturn(customerEntity);
        when(customerRepository.save(customerEntity)).thenReturn(customerEntity);
        when(customerMapper.toDto(customerEntity)).thenReturn(savedDto);

        CustomerDto result = customerService.createCustomer(dto);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
    }
}
