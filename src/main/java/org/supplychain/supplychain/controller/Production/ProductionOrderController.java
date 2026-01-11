package org.supplychain.supplychain.controller.Production;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.supplychain.supplychain.dto.Production.ProductionOrderDTO;
import org.supplychain.supplychain.enums.ProductionOrderStatus;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.Production.ProductionOrderService;

@RestController
@RequestMapping("/api/production-orders")
@RequiredArgsConstructor
@Tag(name = "Ordres de Production", description = "API de gestion des ordres de production")
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

        @PostMapping
        @PreAuthorize("hasRole('CHEF_PRODUCTION')")
    @Operation(summary = "Créer un ordre de production",
            description = "Crée un nouvel ordre de production avec calcul automatique du temps estimé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ordre de production créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
        public ResponseEntity<SuccessResponse<ProductionOrderDTO>> createProductionOrder(
            @Valid @RequestBody ProductionOrderDTO dto,
            HttpServletRequest request) {

        ProductionOrderDTO createdOrder = productionOrderService.createProductionOrder(dto);
        SuccessResponse<ProductionOrderDTO> response = SuccessResponse.of(
                HttpStatus.CREATED,
                "Ordre de production créé avec succès",
                createdOrder,
                request.getRequestURI()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('CHEF_PRODUCTION')")
    @Operation(summary = "Modifier un ordre existant",
            description = "Met à jour un ordre de production existant et recalcule le temps estimé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordre de production mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Ordre de production non trouvé")
    })
        public ResponseEntity<SuccessResponse<ProductionOrderDTO>> updateProductionOrder(
            @Parameter(description = "ID de l'ordre de production") @PathVariable Long id,
            @Valid @RequestBody ProductionOrderDTO dto,
            HttpServletRequest request) {

        ProductionOrderDTO updatedOrder = productionOrderService.updateProductionOrder(id, dto);
        SuccessResponse<ProductionOrderDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Ordre de production mis à jour avec succès",
                updatedOrder,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('CHEF_PRODUCTION')")
    @Operation(summary = "Annuler un ordre si non commencé",
            description = "Annule un ordre de production uniquement s'il est EN_ATTENTE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordre de production annulé avec succès"),
            @ApiResponse(responseCode = "404", description = "Ordre de production non trouvé"),
            @ApiResponse(responseCode = "409", description = "L'ordre ne peut pas être annulé (statut différent de EN_ATTENTE)")
    })
        public ResponseEntity<SuccessResponse<Void>> cancelProductionOrder(
            @Parameter(description = "ID de l'ordre de production") @PathVariable Long id,
            HttpServletRequest request) {

        productionOrderService.cancelProductionOrder(id);
        SuccessResponse<Void> response = SuccessResponse.of(
                HttpStatus.OK,
                "Ordre de production annulé avec succès",
                null,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un ordre de production",
            description = "Récupère un ordre de production par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordre de production récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Ordre de production non trouvé")
    })
    public ResponseEntity<SuccessResponse<ProductionOrderDTO>> getProductionOrderById(
            @Parameter(description = "ID de l'ordre de production") @PathVariable Long id,
            HttpServletRequest request) {

        ProductionOrderDTO order = productionOrderService.getProductionOrderById(id);
        SuccessResponse<ProductionOrderDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Ordre de production récupéré avec succès",
                order,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

        @GetMapping
        @PreAuthorize("hasRole('SUPERVISEUR_PRODUCTION')")
    @Operation(summary = "Consulter la liste complète des ordres",
            description = "Récupère tous les ordres de production avec pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
        public ResponseEntity<SuccessResponse<Page<ProductionOrderDTO>>> getAllProductionOrders(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "idOrder") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "desc") String sortDirection,
            HttpServletRequest request) {

        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductionOrderDTO> orders = productionOrderService.getAllProductionOrders(pageable);

        SuccessResponse<Page<ProductionOrderDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Liste des ordres de production récupérée avec succès",
                orders,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }

        @GetMapping("/status/{status}")
        @PreAuthorize("hasRole('SUPERVISEUR_PRODUCTION')")
    @Operation(summary = "Suivre le statut des ordres",
            description = "Récupère les ordres de production par statut : EN_ATTENTE, EN_PRODUCTION, TERMINE, BLOQUE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ordres récupérés avec succès"),
            @ApiResponse(responseCode = "400", description = "Statut invalide")
    })
        public ResponseEntity<SuccessResponse<Page<ProductionOrderDTO>>> getProductionOrdersByStatus(
            @Parameter(description = "Statut de l'ordre (EN_ATTENTE, EN_PRODUCTION, TERMINE, BLOQUE)")
            @PathVariable ProductionOrderStatus status,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("idOrder").descending());
        Page<ProductionOrderDTO> orders = productionOrderService.getProductionOrdersByStatus(status, pageable);

        SuccessResponse<Page<ProductionOrderDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Ordres avec statut " + status + " récupérés avec succès",
                orders,
                request.getRequestURI()
        );
        return ResponseEntity.ok(response);
    }
}