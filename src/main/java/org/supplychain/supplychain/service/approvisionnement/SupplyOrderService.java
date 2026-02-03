package org.supplychain.supplychain.service.approvisionnement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.Approvisionnement.OrderRequest;
import org.supplychain.supplychain.dto.Approvisionnement.OrderResponse;
import org.supplychain.supplychain.dto.Approvisionnement.OrderLineResponse;
import org.supplychain.supplychain.enums.SupplyOrderStatus;
import java.util.List;

public interface SupplyOrderService {
    OrderResponse createSupplyOrder(OrderRequest request);

    OrderResponse updateSupplyOrder(Long id, OrderRequest request);

    void deleteSupplyOrder(Long id);

    Page<OrderResponse> getAllSupplyOrders(Pageable pageable);

    Page<OrderResponse> getSupplyOrdersByStatus(SupplyOrderStatus status, Pageable pageable);

    OrderResponse getSupplyOrderById(Long id);

    List<OrderLineResponse> getOrderLines(Long orderId);

    OrderResponse updateStatus(Long id, SupplyOrderStatus status);
}