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
import org.supplychain.supplychain.dto.Livraison.DeliveryDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.Livraison.DeliveryService;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Livraisons", description = "API de gestion des livraisons")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "Créer une livraison", description = "Crée une nouvelle livraison")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livraison créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<SuccessResponse<DeliveryDTO>> createDelivery(
            @Parameter(description = "Données de la livraison", required = true)
            @Valid @RequestBody DeliveryDTO deliveryDTO,
            HttpServletRequest request) {

        DeliveryDTO createdDelivery = deliveryService.createDelivery(deliveryDTO);

        SuccessResponse<DeliveryDTO> response = SuccessResponse.of(
                HttpStatus.CREATED,
                "Livraison créée avec succès",
                createdDelivery,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Modifier une livraison", description = "Met à jour les informations d'une livraison")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livraison modifiée avec succès"),
            @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<DeliveryDTO>> updateDelivery(
            @Parameter(description = "ID de la livraison", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de la livraison", required = true)
            @Valid @RequestBody DeliveryDTO deliveryDTO,
            HttpServletRequest request) {

        DeliveryDTO updatedDelivery = deliveryService.updateDelivery(id, deliveryDTO);

        SuccessResponse<DeliveryDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Livraison modifiée avec succès",
                updatedDelivery,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Supprimer une livraison", description = "Supprime une livraison du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livraison supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteDelivery(
            @Parameter(description = "ID de la livraison à supprimer", required = true)
            @PathVariable Long id,
            HttpServletRequest request) {

        deliveryService.deleteDelivery(id);

        SuccessResponse<Void> response = SuccessResponse.of(
                HttpStatus.OK,
                "Livraison supprimée avec succès",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Lister toutes les livraisons", description = "Récupère la liste paginée de toutes les livraisons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<DeliveryDTO>>> getAllDeliveries(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "idDelivery") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<DeliveryDTO> deliveries = deliveryService.getAllDeliveries(pageable);

        SuccessResponse<Page<DeliveryDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Liste des livraisons récupérée avec succès",
                deliveries,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer une livraison", description = "Récupère les détails d'une livraison par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livraison trouvée"),
            @ApiResponse(responseCode = "404", description = "Livraison non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<DeliveryDTO>> getDeliveryById(
            @Parameter(description = "ID de la livraison", required = true)
            @PathVariable Long id,
            HttpServletRequest request) {

        DeliveryDTO delivery = deliveryService.getDeliveryById(id);

        SuccessResponse<DeliveryDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Livraison récupérée avec succès",
                delivery,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Récupérer une livraison par commande", description = "Récupère la livraison associée à une commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livraison trouvée"),
            @ApiResponse(responseCode = "404", description = "Livraison non trouvée pour cette commande")
    })
    @GetMapping("/order/{orderId}")
    public ResponseEntity<SuccessResponse<DeliveryDTO>> getDeliveryByOrderId(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long orderId,
            HttpServletRequest request) {

        DeliveryDTO delivery = deliveryService.getDeliveryByOrderId(orderId);

        SuccessResponse<DeliveryDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Livraison de la commande récupérée avec succès",
                delivery,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }
}