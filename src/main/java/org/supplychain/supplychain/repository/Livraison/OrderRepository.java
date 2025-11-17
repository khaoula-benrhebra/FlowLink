package org.supplychain.supplychain.repository.Livraison;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.enums.OrderStatus;
import org.supplychain.supplychain.model.Order;

import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAll(Pageable pageable);

    // Solution alternative avec un nom de méthode différent
    @Query("SELECT o FROM Order o WHERE o.status = :status")
    Page<Order> findOrdersByStatus(@Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.customer.idCustomer = :customerId AND o.status != 'LIVREE'")
    boolean hasActiveOrders(@Param("customerId") Long customerId);
}