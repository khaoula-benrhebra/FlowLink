package org.supplychain.supplychain.service.Livraison;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.Livraison.CustomerDTO;

public interface CustomerService {

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);

    void deleteCustomer(Long id);

    Page<CustomerDTO> getAllCustomers(Pageable pageable);

    Page<CustomerDTO> searchCustomersByName(String name, Pageable pageable);

    CustomerDTO getCustomerById(Long id);
}