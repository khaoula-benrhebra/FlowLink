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
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.Approvisionnement.SupplyOrderDTO;
import org.supplychain.supplychain.dto.Approvisionnement.SupplyOrderLineDTO;
import org.supplychain.supplychain.enums.SupplyOrderStatus;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.approvisionnement.SupplyOrderService;

import java.util.List;

@RestController
@RequestMapping("/api/supply-orders")
@RequiredArgsConstructor
@Tag(name = "Commandes d'Approvisionnement", description = "API de gestion des commandes d'approvisionnement")
public class SupplyOrderController {

    private final SupplyOrderService supplyOrderService;


    @Operation(summary = "Créer une commande d'approvisionnement", description = "Crée une nouvelle commande d'approvisionnement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Commande créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<SupplyOrderDTO>> createSupplyOrder(
            @Parameter(description = "Données de la commande d'approvisionnement", required = true)
            @Valid @RequestBody SupplyOrderDTO supplyOrderDTO,
            HttpServletRequest request) {

        SupplyOrderDTO createdOrder = supplyOrderService.createSupplyOrder(supplyOrderDTO);

        SuccessResponse<SupplyOrderDTO> response = SuccessResponse.of(
                HttpStatus.CREATED,
                "Commande d'approvisionnement créée avec succès",
                createdOrder,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Modifier une commande d'approvisionnement", description = "Met à jour une commande d'approvisionnement existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande modifiée avec succès"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<SupplyOrderDTO>> updateSupplyOrder(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de la commande", required = true)
            @Valid @RequestBody SupplyOrderDTO supplyOrderDTO,
            HttpServletRequest request) {

        SupplyOrderDTO updatedOrder = supplyOrderService.updateSupplyOrder(id, supplyOrderDTO);

        SuccessResponse<SupplyOrderDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Commande d'approvisionnement modifiée avec succès",
                updatedOrder,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Supprimer une commande d'approvisionnement", description = "Supprime une commande d'approvisionnement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteSupplyOrder(
            @Parameter(description = "ID de la commande à supprimer", required = true)
            @PathVariable Long id,
            HttpServletRequest request) {

        supplyOrderService.deleteSupplyOrder(id);

        SuccessResponse<Void> response = SuccessResponse.of(
                HttpStatus.OK,
                "Commande d'approvisionnement supprimée avec succès",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Lister toutes les commandes d'approvisionnement", description = "Récupère la liste paginée de toutes les commandes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<SupplyOrderDTO>>> getAllSupplyOrders(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "orderDate") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<SupplyOrderDTO> orders = supplyOrderService.getAllSupplyOrders(pageable);

        SuccessResponse<Page<SupplyOrderDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Liste des commandes d'approvisionnement récupérée avec succès",
                orders,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Filtrer les commandes par statut", description = "Récupère les commandes d'approvisionnement par statut")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commandes filtrées avec succès")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<SuccessResponse<Page<SupplyOrderDTO>>> getSupplyOrdersByStatus(
            @Parameter(description = "Statut de la commande", required = true)
            @PathVariable SupplyOrderStatus status,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "orderDate") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<SupplyOrderDTO> orders = supplyOrderService.getSupplyOrdersByStatus(status, pageable);

        SuccessResponse<Page<SupplyOrderDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Commandes filtrées par statut récupérées avec succès",
                orders,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer une commande d'approvisionnement", description = "Récupère les détails d'une commande par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande trouvée"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<SupplyOrderDTO>> getSupplyOrderById(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long id,
            HttpServletRequest request) {

        SupplyOrderDTO order = supplyOrderService.getSupplyOrderById(id);

        SuccessResponse<SupplyOrderDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Commande d'approvisionnement récupérée avec succès",
                order,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Récupérer les lignes de commande", description = "Récupère toutes les lignes d'une commande d'approvisionnement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lignes de commande récupérées avec succès"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    @GetMapping("/{orderId}/lines")
    public ResponseEntity<SuccessResponse<List<SupplyOrderLineDTO>>> getOrderLines(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long orderId,
            HttpServletRequest request) {

        List<SupplyOrderLineDTO> orderLines = supplyOrderService.getOrderLines(orderId);

        SuccessResponse<List<SupplyOrderLineDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Lignes de commande récupérées avec succès",
                orderLines,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }
}