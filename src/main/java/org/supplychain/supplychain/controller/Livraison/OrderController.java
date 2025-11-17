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
import org.supplychain.supplychain.dto.Livraison.OrderDTO;
import org.supplychain.supplychain.dto.Livraison.ProductOrderDTO;
import org.supplychain.supplychain.enums.OrderStatus;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.Livraison.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
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

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<OrderDTO>> updateOrder(
            @PathVariable Long id,
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

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> cancelOrder(
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

    @GetMapping
    public ResponseEntity<SuccessResponse<Page<OrderDTO>>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idOrder") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
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

    @GetMapping("/status/{status}")
    public ResponseEntity<SuccessResponse<Page<OrderDTO>>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "idOrder") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
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
    public ResponseEntity<SuccessResponse<OrderDTO>> getOrderById(
            @PathVariable Long id,
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

    @GetMapping("/{orderId}/products")
    public ResponseEntity<SuccessResponse<List<ProductOrderDTO>>> getOrderProducts(
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