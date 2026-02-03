package org.supplychain.supplychain.service.Livraison;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.Livraison.CustomerRequestDTO;
import org.supplychain.supplychain.dto.Livraison.CustomerResponseDTO;
public interface CustomerService {
    CustomerResponseDTO createCustomer(CustomerRequestDTO customerDTO);
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerDTO);
    void deleteCustomer(Long id);
    Page<CustomerResponseDTO> getAllCustomers(String search, Pageable pageable);
    CustomerResponseDTO getCustomerById(Long id);
    // Récupère le détail avec statistiques
    CustomerResponseDTO getCustomerByIdWithStats(Long id);
}