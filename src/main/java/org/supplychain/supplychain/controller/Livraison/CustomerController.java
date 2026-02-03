package org.supplychain.supplychain.controller.Livraison;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.supplychain.supplychain.dto.Livraison.CustomerRequestDTO;
import org.supplychain.supplychain.dto.Livraison.CustomerResponseDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.Livraison.CustomerService;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "API de gestion des clients")
public class CustomerController {
        private final CustomerService customerService;

        @PostMapping
        @Operation(summary = "Créer un nouveau client")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "409", description = "Email ou nom déjà existant")
        })
        public ResponseEntity<SuccessResponse<CustomerResponseDTO>> createCustomer(
                        @Valid @RequestBody CustomerRequestDTO customerDTO,
                        HttpServletRequest request) {

                CustomerResponseDTO createdCustomer = customerService.createCustomer(customerDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(
                                SuccessResponse.of(HttpStatus.CREATED, "Client créé avec succès", createdCustomer,
                                                request.getRequestURI()));
        }

        @PutMapping("/{id}")
        @Operation(summary = "Modifier un client")
        public ResponseEntity<SuccessResponse<CustomerResponseDTO>> updateCustomer(
                        @PathVariable Long id,
                        @Valid @RequestBody CustomerRequestDTO customerDTO,
                        HttpServletRequest request) {
                CustomerResponseDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
                return ResponseEntity.ok(
                                SuccessResponse.of(HttpStatus.OK, "Client modifié avec succès", updatedCustomer,
                                                request.getRequestURI()));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Supprimer un client")
        public ResponseEntity<SuccessResponse<Void>> deleteCustomer(
                        @PathVariable Long id,
                        HttpServletRequest request) {
                customerService.deleteCustomer(id);
                return ResponseEntity.ok(
                                SuccessResponse.of(HttpStatus.OK, "Client supprimé avec succès", null,
                                                request.getRequestURI()));
        }

        @GetMapping
        @Operation(summary = "Lister tous les clients avec recherche optionnelle")
        public ResponseEntity<SuccessResponse<Page<CustomerResponseDTO>>> getAllCustomers(
                        @Parameter(description = "Terme de recherche (name, address, city)") @RequestParam(required = false) String search,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "name") String sortBy,
                        @RequestParam(defaultValue = "asc") String direction,
                        HttpServletRequest request) {
                Sort.Direction sortDirection = direction.equalsIgnoreCase("desc")
                                ? Sort.Direction.DESC
                                : Sort.Direction.ASC;
                Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
                Page<CustomerResponseDTO> customers = customerService.getAllCustomers(search, pageable);
                return ResponseEntity.ok(
                                SuccessResponse.of(HttpStatus.OK, "Liste des clients récupérée avec succès", customers,
                                                request.getRequestURI()));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Récupérer un client par ID avec statistiques")
        public ResponseEntity<SuccessResponse<CustomerResponseDTO>> getCustomerById(
                        @PathVariable Long id,
                        HttpServletRequest request) {
                CustomerResponseDTO customer = customerService.getCustomerByIdWithStats(id);
                return ResponseEntity.ok(
                                SuccessResponse.of(HttpStatus.OK, "Client récupéré avec succès", customer,
                                                request.getRequestURI()));
        }
}