package org.supplychain.supplychain.controller.approvisionnement;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.Approvisionnement.SupplierDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.approvisionnement.SupplierService;


@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Fournisseurs", description = "API de gestion des fournisseurs")
public class SupplierController {

    private final SupplierService supplierService;


    @Operation(summary = "Créer un fournisseur", description = "Ajoute un nouveau fournisseur au système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fournisseur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<SuccessResponse<SupplierDTO>> createSupplier(
            @Parameter(description = "Données du fournisseur à créer", required = true)
            @Valid @RequestBody SupplierDTO supplierDTO,
            HttpServletRequest request) {

        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);

        SuccessResponse<SupplierDTO> response = SuccessResponse.of(
                HttpStatus.CREATED,
                "Fournisseur créé avec succès",
                createdSupplier,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Modifier un fournisseur", description = "Met à jour les informations d'un fournisseur existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fournisseur modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Fournisseur non trouvé")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<SuccessResponse<SupplierDTO>> updateSupplier(
            @Parameter(description = "ID du fournisseur", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données du fournisseur", required = true)
            @Valid @RequestBody SupplierDTO supplierDTO,
            HttpServletRequest request) {

        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);

        SuccessResponse<SupplierDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Fournisseur modifié avec succès",
                updatedSupplier,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Supprimer un fournisseur", description = "Supprime un fournisseur du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fournisseur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Fournisseur non trouvé")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTIONNAIRE_APPROVISIONNEMENT')")
    public ResponseEntity<SuccessResponse<Void>> deleteSupplier(
            @Parameter(description = "ID du fournisseur à supprimer", required = true)
            @PathVariable Long id,
            HttpServletRequest request) {

        supplierService.deleteSupplier(id);

        SuccessResponse<Void> response = SuccessResponse.of(
                HttpStatus.OK,
                "Fournisseur supprimé avec succès",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Lister tous les fournisseurs", description = "Récupère la liste paginée de tous les fournisseurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping
    @PreAuthorize("hasRole('SUPERVISEUR_LOGISTIQUE')")
    public ResponseEntity<SuccessResponse<Page<SupplierDTO>>> getAllSuppliers(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<SupplierDTO> suppliers = supplierService.getAllSuppliers(pageable);

        SuccessResponse<Page<SupplierDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Liste des fournisseurs récupérée avec succès",
                suppliers,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Rechercher des fournisseurs", description = "Recherche des fournisseurs par nom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recherche effectuée avec succès")
    })
    @GetMapping("/search")
    @PreAuthorize("hasRole('RESPONSABLE_ACHATS')")
    public ResponseEntity<SuccessResponse<Page<SupplierDTO>>> searchSuppliersByName(
            @Parameter(description = "Nom du fournisseur à rechercher", required = true)
            @RequestParam String name,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<SupplierDTO> suppliers = supplierService.searchSuppliersByName(name, pageable);

        SuccessResponse<Page<SupplierDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Recherche de fournisseurs effectuée avec succès",
                suppliers,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Récupérer un fournisseur", description = "Récupère les détails d'un fournisseur par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fournisseur trouvé"),
            @ApiResponse(responseCode = "404", description = "Fournisseur non trouvé")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE_APPROVISIONNEMENT', 'SUPERVISEUR_LOGISTIQUE', 'RESPONSABLE_ACHATS')")
    public ResponseEntity<SuccessResponse<SupplierDTO>> getSupplierById(
            @Parameter(description = "ID du fournisseur", required = true)
            @PathVariable Long id,
            HttpServletRequest request) {

        SupplierDTO supplier = supplierService.getSupplierById(id);

        SuccessResponse<SupplierDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Fournisseur récupéré avec succès",
                supplier,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }
}