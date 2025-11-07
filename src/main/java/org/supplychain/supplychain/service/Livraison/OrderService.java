package org.supplychain.supplychain.service.Livraison;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.Livraison.OrderDTO;
import org.supplychain.supplychain.dto.Livraison.ProductOrderDTO;
import org.supplychain.supplychain.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderDTO createOrder(OrderDTO orderDTO);

    OrderDTO updateOrder(Long id, OrderDTO orderDTO);

    void cancelOrder(Long id);

    Page<OrderDTO> getAllOrders(Pageable pageable);

    Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable);

    OrderDTO getOrderById(Long id);

    List<ProductOrderDTO> getOrderProducts(Long orderId);
}