package org.supplychain.supplychain.repository.Livraison;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.model.ProductOrder;

import java.util.List;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {

    List<ProductOrder> findByOrder_IdOrder(Long orderId);

    void deleteByOrder_IdOrder(Long orderId);

    @Query("SELECT COUNT(po) > 0 FROM ProductOrder po WHERE po.product.idProduct = :productId")
    boolean isProductUsedInOrders(@Param("productId") Long productId);
}