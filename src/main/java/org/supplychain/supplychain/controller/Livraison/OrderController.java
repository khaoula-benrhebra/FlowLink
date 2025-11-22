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
import org.supplychain.supplychain.dto.Livraison.OrderDTO;
import org.supplychain.supplychain.dto.Livraison.ProductOrderDTO;
import org.supplychain.supplychain.enums.OrderStatus;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.Livraison.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Commandes", description = "API de gestion des commandes clients")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle commande", description = "Crée une nouvelle commande client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Commande créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<SuccessResponse<OrderDTO>> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            HttpServletRequest request) {

        OrderDTO createdOrder = orderService.createOrder(orderDTO);

        SuccessResponse<OrderDTO> response = SuccessResponse.of(
                HttpStatus.CREATED,
                "Commande client créée avec succès",
                createdOrder,
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Modifier une commande", description = "Met à jour les informations d'une commande client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande modifiée avec succès"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<OrderDTO>> updateOrder(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de la commande", required = true)
            @Valid @RequestBody OrderDTO orderDTO,
            HttpServletRequest request) {

        OrderDTO updatedOrder = orderService.updateOrder(id, orderDTO);

        SuccessResponse<OrderDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Commande client modifiée avec succès",
                updatedOrder,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Annuler une commande", description = "Annule une commande client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande annulée avec succès"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> cancelOrder(
            @Parameter(description = "ID de la commande à annuler", required = true)
            @PathVariable Long id,
            HttpServletRequest request) {

        orderService.cancelOrder(id);

        SuccessResponse<Void> response = SuccessResponse.of(
                HttpStatus.OK,
                "Commande annulée avec succès",
                null,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lister toutes les commandes", description = "Récupère la liste paginée de toutes les commandes clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<Page<OrderDTO>>> getAllOrders(
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "idOrder") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<OrderDTO> orders = orderService.getAllOrders(pageable);

        SuccessResponse<Page<OrderDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Liste des commandes récupérée avec succès",
                orders,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Filtrer les commandes par statut", description = "Récupère les commandes clients par statut")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commandes filtrées avec succès")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<SuccessResponse<Page<OrderDTO>>> getOrdersByStatus(
            @Parameter(description = "Statut de la commande", required = true)
            @PathVariable OrderStatus status,
            @Parameter(description = "Numéro de page") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "idOrder") String sortBy,
            @Parameter(description = "Direction du tri") @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest request) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<OrderDTO> orders = orderService.getOrdersByStatus(status, pageable);

        SuccessResponse<Page<OrderDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Commandes filtrées par statut récupérées avec succès",
                orders,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une commande par ID", description = "Récupère les détails d'une commande spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Commande trouvée"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    public ResponseEntity<SuccessResponse<OrderDTO>> getOrderById(
            @Parameter(description = "ID de la commande") @PathVariable Long id,
            HttpServletRequest request) {

        OrderDTO order = orderService.getOrderById(id);

        SuccessResponse<OrderDTO> response = SuccessResponse.of(
                HttpStatus.OK,
                "Commande récupérée avec succès",
                order,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Récupérer les produits d'une commande", description = "Récupère tous les produits associés à une commande")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produits récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    @GetMapping("/{orderId}/products")
    public ResponseEntity<SuccessResponse<List<ProductOrderDTO>>> getOrderProducts(
            @Parameter(description = "ID de la commande", required = true)
            @PathVariable Long orderId,
            HttpServletRequest request) {

        List<ProductOrderDTO> productOrders = orderService.getOrderProducts(orderId);

        SuccessResponse<List<ProductOrderDTO>> response = SuccessResponse.of(
                HttpStatus.OK,
                "Produits de la commande récupérés avec succès",
                productOrders,
                request.getRequestURI()
        );

        return ResponseEntity.ok(response);
    }
}