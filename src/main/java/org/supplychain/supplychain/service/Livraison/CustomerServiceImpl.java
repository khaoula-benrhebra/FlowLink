package org.supplychain.supplychain.service.Livraison;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Livraison.CustomerDTO;
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
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        if (customerRepository.findByEmail(customerDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
        }

        if (customerRepository.findByName(customerDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Customer", "name", customerDTO.getName());
        }

        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        customerRepository.findByEmail(customerDTO.getEmail())
                .ifPresent(customer -> {
                    if (!customer.getIdCustomer().equals(id)) {
                        throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
                    }
                });

        customerRepository.findByName(customerDTO.getName())
                .ifPresent(customer -> {
                    if (!customer.getIdCustomer().equals(id)) {
                        throw new DuplicateResourceException("Customer", "name", customerDTO.getName());
                    }
                });

        customerMapper.updateEntityFromDTO(customerDTO, existingCustomer);
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return customerMapper.toDTO(updatedCustomer);
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
    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        Page<Customer> customers = customerRepository.findAll(pageable);
        return customers.map(customerMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> searchCustomersByName(String name, Pageable pageable) {
        Page<Customer> customers = customerRepository.findByNameContainingIgnoreCase(name, pageable);
        return customers.map(customerMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return customerMapper.toDTO(customer);
    }
}