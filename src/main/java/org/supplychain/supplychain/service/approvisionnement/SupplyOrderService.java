package org.supplychain.supplychain.service.approvisionnement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.Approvisionnement.SupplyOrderDTO;
import org.supplychain.supplychain.dto.Approvisionnement.SupplyOrderLineDTO;
import org.supplychain.supplychain.enums.SupplyOrderStatus;

import java.util.List;

public interface SupplyOrderService {

    SupplyOrderDTO createSupplyOrder(SupplyOrderDTO supplyOrderDTO);

    SupplyOrderDTO updateSupplyOrder(Long id, SupplyOrderDTO supplyOrderDTO);

    void deleteSupplyOrder(Long id);

    Page<SupplyOrderDTO> getAllSupplyOrders(Pageable pageable);

    Page<SupplyOrderDTO> getSupplyOrdersByStatus(SupplyOrderStatus status, Pageable pageable);

    SupplyOrderDTO getSupplyOrderById(Long id);

    List<SupplyOrderLineDTO> getOrderLines(Long orderId);
}