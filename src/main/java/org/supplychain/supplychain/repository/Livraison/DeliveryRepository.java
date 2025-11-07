package org.supplychain.supplychain.repository.Livraison;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.model.Delivery;

import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByDeliveryNumber(String deliveryNumber);

    Page<Delivery> findAll(Pageable pageable);

    @Query("SELECT COUNT(d) > 0 FROM Delivery d WHERE d.order.idOrder = :orderId")
    boolean hasDeliveryForOrder(@Param("orderId") Long orderId);

    Optional<Delivery> findByOrder_IdOrder(Long orderId);
}