package org.supplychain.supplychain.service.Livraison;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Livraison.CustomerRequestDTO;
import org.supplychain.supplychain.dto.Livraison.CustomerResponseDTO;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Livraison.CustomerMapper;
import org.supplychain.supplychain.model.Customer;
import org.supplychain.supplychain.repository.Livraison.CustomerRepository;
import org.supplychain.supplychain.repository.Livraison.OrderRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO) {
        if (customerRepository.findByEmail(customerDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
        }
        if (customerRepository.findByName(customerDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Customer", "name", customerDTO.getName());
        }

        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponseDTO(savedCustomer);
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        customerRepository.findByEmail(customerDTO.getEmail())
                .filter(c -> !c.getIdCustomer().equals(id))
                .ifPresent(c -> {
                    throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
                });
        customerRepository.findByName(customerDTO.getName())
                .filter(c -> !c.getIdCustomer().equals(id))
                .ifPresent(c -> {
                    throw new DuplicateResourceException("Customer", "name", customerDTO.getName());
                });
        customerMapper.updateEntityFromDTO(customerDTO, existingCustomer);
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMapper.toResponseDTO(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer", "id", id);
        }
        if (orderRepository.hasActiveOrders(id)) {
            throw new ResourceInUseException("Customer", id, "active orders");
        }
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(String search, Pageable pageable) {
        Page<Customer> customers;
        if (search != null && !search.trim().isEmpty()) {
            customers = customerRepository.searchByKeyword(search.trim(), pageable);
        } else {
            customers = customerRepository.findAll(pageable);
        }
        return customers.map(customerMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return customerMapper.toResponseDTO(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerByIdWithStats(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        CustomerResponseDTO dto = customerMapper.toResponseDTO(customer);
        dto.setOrdersCount(orderRepository.countByCustomerId(id));
        dto.setHasActiveOrders(orderRepository.hasActiveOrders(id));
        return dto;
    }
}