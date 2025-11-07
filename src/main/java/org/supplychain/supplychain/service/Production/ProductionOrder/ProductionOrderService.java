package org.supplychain.supplychain.service.Production.ProductionOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.Production.ProductionOrderDTO;
import org.supplychain.supplychain.enums.ProductionOrderStatus;

public interface ProductionOrderService {

    ProductionOrderDTO createProductionOrder(ProductionOrderDTO dto);

    ProductionOrderDTO updateProductionOrder(Long id, ProductionOrderDTO dto);

    void cancelProductionOrder(Long id);

    Page<ProductionOrderDTO> getAllProductionOrders(Pageable pageable);

    Page<ProductionOrderDTO> getProductionOrdersByStatus(ProductionOrderStatus status, Pageable pageable);

    ProductionOrderDTO getProductionOrderById(Long id);
}