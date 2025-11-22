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
import org.supplychain.supplychain.dto.Livraison.CustomerDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.Livraison.CustomerService;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "API de gestion des clients")
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping
    @Operation(summary = "Créer un nouveau client", description = "Ajoute un nouveau client dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
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


    @Operation(summary = "Modifier un client", description = "Met à jour les informations d'un client existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<CustomerDTO>> updateCustomer(
            @Parameter(description = "ID du client", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données du client", required = true)
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


    @Operation(summary = "Supprimer un client", description = "Supprime un client du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteCustomer(
            @Parameter(description = "ID du client à supprimer", required = true)
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
    @Operation(summary = "Lister tous les clients", description = "Récupère la liste paginée de tous les clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    public ResponseEntity<SuccessResponse<Page<CustomerDTO>>> getAllCustomers(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "asc") String direction,
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

    @Operation(summary = "Rechercher des clients", description = "Recherche des clients par nom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès")
    })
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<Page<CustomerDTO>>> searchCustomersByName(
            @Parameter(description = "Nom du client à rechercher", required = true)
            @RequestParam String name,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "asc") String direction,
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
    @Operation(summary = "Récupérer un client par ID", description = "Récupère les détails d'un client spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    public ResponseEntity<SuccessResponse<CustomerDTO>> getCustomerById(
            @Parameter(description = "ID du client") @PathVariable Long id,
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