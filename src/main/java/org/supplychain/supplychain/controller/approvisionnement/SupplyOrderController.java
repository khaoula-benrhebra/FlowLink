package org.supplychain.supplychain.controller.approvisionnement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.supplychain.supplychain.dto.Approvisionnement.*;
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
    @PreAuthorize("hasRole('RESPONSABLE_ACHATS')")
    public ResponseEntity<SuccessResponse<OrderResponse>> createSupplyOrder(
            @Valid @RequestBody OrderRequest request, HttpServletRequest r) {
        OrderResponse res = supplyOrderService.createSupplyOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.of(HttpStatus.CREATED, "Commande créée", res, r.getRequestURI()));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESPONSABLE_ACHATS')")
    public ResponseEntity<SuccessResponse<OrderResponse>> updateSupplyOrder(
            @PathVariable Long id, @Valid @RequestBody OrderRequest request, HttpServletRequest r) {
        OrderResponse res = supplyOrderService.updateSupplyOrder(id, request);
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "Commande modifiée", res, r.getRequestURI()));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RESPONSABLE_ACHATS')")
    public ResponseEntity<SuccessResponse<Void>> deleteSupplyOrder(@PathVariable Long id, HttpServletRequest r) {
        supplyOrderService.deleteSupplyOrder(id);
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "Commande supprimée", null, r.getRequestURI()));
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS', 'SUPERVISEUR_LOGISTIQUE')")
    public ResponseEntity<SuccessResponse<Page<OrderResponse>>> getAllSupplyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String dir,
            HttpServletRequest r) {
        Sort.Direction direction = dir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "Liste des commandes", supplyOrderService.getAllSupplyOrders(pageable), r.getRequestURI()));
    }
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS', 'SUPERVISEUR_LOGISTIQUE')")
    public ResponseEntity<SuccessResponse<Page<OrderResponse>>> getSupplyOrdersByStatus(
            @PathVariable SupplyOrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest r) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "Commandes par statut", supplyOrderService.getSupplyOrdersByStatus(status, pageable), r.getRequestURI()));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS', 'SUPERVISEUR_LOGISTIQUE')")
    public ResponseEntity<SuccessResponse<OrderResponse>> getSupplyOrderById(@PathVariable Long id, HttpServletRequest r) {
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "Détails de la commande", supplyOrderService.getSupplyOrderById(id), r.getRequestURI()));
    }
    @GetMapping("/{orderId}/lines")
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS', 'SUPERVISEUR_LOGISTIQUE')")
    public ResponseEntity<SuccessResponse<List<OrderLineResponse>>> getOrderLines(@PathVariable Long orderId, HttpServletRequest r) {
        return ResponseEntity.ok(SuccessResponse.of(HttpStatus.OK, "Lignes de commande", supplyOrderService.getOrderLines(orderId), r.getRequestURI()));
    }
}