package org.supplychain.supplychain.repository.Production;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.enums.ProductionOrderStatus;
import org.supplychain.supplychain.model.ProductionOrder;

@Repository
public interface ProductionOrderRepository extends JpaRepository<ProductionOrder, Long> {

    Page<ProductionOrder> findByStatus(ProductionOrderStatus status, Pageable pageable);

    boolean existsByProduct_IdProduct(Long productId);
}