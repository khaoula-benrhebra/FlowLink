package org.supplychain.supplychain.service.Livraison;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Livraison.OrderDTO;
import org.supplychain.supplychain.dto.Livraison.ProductOrderDTO;
import org.supplychain.supplychain.enums.OrderStatus;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Livraison.OrderMapper;
import org.supplychain.supplychain.mapper.Livraison.ProductOrderMapper;
import org.supplychain.supplychain.model.Customer;
import org.supplychain.supplychain.model.Order;
import org.supplychain.supplychain.model.Product;
import org.supplychain.supplychain.model.ProductOrder;
import org.supplychain.supplychain.repository.Livraison.CustomerRepository;
import org.supplychain.supplychain.repository.Livraison.OrderRepository;
import org.supplychain.supplychain.repository.Livraison.ProductOrderRepository;
import org.supplychain.supplychain.repository.Production.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductOrderRepository productOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final ProductOrderMapper productOrderMapper;

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", orderDTO.getCustomerId()));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(orderDTO.getStatus());

    List<ProductOrder> orderLines = new ArrayList<>();

    List<ProductOrderDTO> productOrders = orderDTO.getProductOrders();

    for (ProductOrderDTO lineDTO : productOrders) {
            Product product = productRepository.findById(lineDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", lineDTO.getProductId()));

            ProductOrder productOrder = new ProductOrder();
            productOrder.setOrder(order);
            productOrder.setProduct(product);
            productOrder.setQuantity(lineDTO.getQuantity());
            productOrder.setUnitPrice(lineDTO.getUnitPrice());

            BigDecimal lineTotal = lineDTO.getUnitPrice().multiply(BigDecimal.valueOf(lineDTO.getQuantity()));
            productOrder.setTotalPrice(lineTotal);

            orderLines.add(productOrder);
        }

        order.setProductOrders(orderLines);

        Order savedOrder = orderRepository.save(order);

        OrderDTO result = orderMapper.toDTO(savedOrder);
        return result;
    }

    @Override
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (existingOrder.getStatus() == OrderStatus.EN_ROUTE ||
                existingOrder.getStatus() == OrderStatus.LIVREE) {
            throw new ResourceInUseException("Cannot update an order that is already shipped or delivered");
        }

        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", orderDTO.getCustomerId()));

        existingOrder.setCustomer(customer);
        existingOrder.setStatus(orderDTO.getStatus());

    existingOrder.getProductOrders().clear();

    List<ProductOrderDTO> productOrders = orderDTO.getProductOrders();

    for (ProductOrderDTO lineDTO : productOrders) {
            Product product = productRepository.findById(lineDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", lineDTO.getProductId()));

            ProductOrder productOrder = new ProductOrder();
            productOrder.setOrder(existingOrder);
            productOrder.setProduct(product);
            productOrder.setQuantity(lineDTO.getQuantity());
            productOrder.setUnitPrice(lineDTO.getUnitPrice());

            BigDecimal lineTotal = lineDTO.getUnitPrice().multiply(BigDecimal.valueOf(lineDTO.getQuantity()));
            productOrder.setTotalPrice(lineTotal);

            existingOrder.getProductOrders().add(productOrder);
        }

        Order updatedOrder = orderRepository.save(existingOrder);

        return orderMapper.toDTO(updatedOrder);
    }

    @Override
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        if (order.getStatus() == OrderStatus.EN_ROUTE || order.getStatus() == OrderStatus.LIVREE) {
            throw new ResourceInUseException("Cannot cancel an order that is already shipped or delivered");
        }

        orderRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(orderMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        // Changer findByStatus par findOrdersByStatus
        Page<Order> orders = orderRepository.findOrdersByStatus(status, pageable);
        return orders.map(orderMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return orderMapper.toDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductOrderDTO> getOrderProducts(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order", "id", orderId);
        }

        List<ProductOrder> productOrders = productOrderRepository.findByOrder_IdOrder(orderId);
        return productOrderMapper.toDTOList(productOrders);
    }
}