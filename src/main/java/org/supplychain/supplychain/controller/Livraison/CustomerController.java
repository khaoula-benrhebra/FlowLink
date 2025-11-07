package org.supplychain.supplychain.controller.Livraison;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.Livraison.CustomerDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.Livraison.CustomerService;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping
    public ResponseEntity<SuccessResponse<CustomerDTO>> createCustomer(
            @Valid @RequestBody CustomerDTO customerDTO,
            HttpServletRequest request) {

        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);

        SuccessResponse<CustomerDTO> response = SuccessResponse.of(
                HttpStatus.CREATED,
                "Client créé avec succès",
                createdCustomer,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<CustomerDTO>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO customerDTO,
            HttpServletRequest request) {

        CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);

        SuccessResponse<CustomerDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Client modifié avec succès",
                updatedCustomer,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteCustomer(
            @PathVariable Long id,
            HttpServletRequest request) {

        customerService.deleteCustomer(id);

        SuccessResponse<Void> response = SuccessResponse.of(
                HttpStatus.OK,
                "Client supprimé avec succès",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<Page<CustomerDTO>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<CustomerDTO> customers = customerService.getAllCustomers(pageable);

        SuccessResponse<Page<CustomerDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Liste des clients récupérée avec succès",
                customers,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<Page<CustomerDTO>>> searchCustomersByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<CustomerDTO> customers = customerService.searchCustomersByName(name, pageable);

        SuccessResponse<Page<CustomerDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Recherche de clients effectuée avec succès",
                customers,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<CustomerDTO>> getCustomerById(
            @PathVariable Long id,
            HttpServletRequest request) {

        CustomerDTO customer = customerService.getCustomerById(id);

        SuccessResponse<CustomerDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Client récupéré avec succès",
                customer,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }
}