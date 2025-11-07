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
import org.supplychain.supplychain.dto.Livraison.DeliveryDTO;
import org.supplychain.supplychain.response.SuccessResponse;
import org.supplychain.supplychain.service.Livraison.DeliveryService;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<SuccessResponse<DeliveryDTO>> createDelivery(
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

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<DeliveryDTO>> updateDelivery(
            @PathVariable Long id,
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


    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteDelivery(
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


    @GetMapping
    public ResponseEntity<SuccessResponse<Page<DeliveryDTO>>> getAllDeliveries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "deliveryDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
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

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<DeliveryDTO>> getDeliveryById(
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


    @GetMapping("/order/{orderId}")
    public ResponseEntity<SuccessResponse<DeliveryDTO>> getDeliveryByOrderId(
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