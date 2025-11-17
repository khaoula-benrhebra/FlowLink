package org.supplychain.supplychain.controller.approvisionnement;

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
public class SupplyOrderController {

    private final SupplyOrderService supplyOrderService;


    @PostMapping
    public ResponseEntity<SuccessResponse<SupplyOrderDTO>> createSupplyOrder(
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

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<SupplyOrderDTO>> updateSupplyOrder(
            @PathVariable Long id,
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


    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteSupplyOrder(
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


    @GetMapping
    public ResponseEntity<SuccessResponse<Page<SupplyOrderDTO>>> getAllSupplyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
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

    @GetMapping("/status/{status}")
    public ResponseEntity<SuccessResponse<Page<SupplyOrderDTO>>> getSupplyOrdersByStatus(
            @PathVariable SupplyOrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
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

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<SupplyOrderDTO>> getSupplyOrderById(
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


    @GetMapping("/{orderId}/lines")
    public ResponseEntity<SuccessResponse<List<SupplyOrderLineDTO>>> getOrderLines(
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