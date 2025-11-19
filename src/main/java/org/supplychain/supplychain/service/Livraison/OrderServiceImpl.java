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
import org.supplychain.supplychain.exception.InsufficientStockException;
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

        Order order = orderMapper.toEntity(orderDTO);
        order.setCustomer(customer);

        List<ProductOrder> orderLines = new ArrayList<>();
        List<ProductOrderDTO> productOrders = orderDTO.getProductOrders();

        // Vérifier le stock pour tous les produits
        boolean allProductsInStock = true;

        for (ProductOrderDTO lineDTO : productOrders) {
            Product product = productRepository.findById(lineDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", lineDTO.getProductId()));

            // Vérifier si le stock est suffisant
            if (product.getStock() < lineDTO.getQuantity()) {
                allProductsInStock = false;
                break;
            }
        }

        // Sauvegarder la commande d'abord
        Order savedOrder = orderRepository.save(order);

        // Déterminer le statut et gérer le stock
        if (allProductsInStock) {
            savedOrder.setStatus(OrderStatus.EN_ROUTE);

            // Déduire le stock pour chaque produit
            for (ProductOrderDTO lineDTO : productOrders) {
                Product product = productRepository.findById(lineDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", lineDTO.getProductId()));

                // Déduire le stock
                int newStock = product.getStock() - lineDTO.getQuantity();
                product.setStock(newStock);
                productRepository.save(product);

                // Créer la ligne de commande via le mapper
                ProductOrder productOrder = productOrderMapper.toEntity(lineDTO);
                productOrder.setOrder(savedOrder);
                productOrder.setProduct(product);

                BigDecimal lineTotal = lineDTO.getUnitPrice().multiply(BigDecimal.valueOf(lineDTO.getQuantity()));
                productOrder.setTotalPrice(lineTotal);

                orderLines.add(productOrder);
            }
        } else {
            savedOrder.setStatus(OrderStatus.EN_PREPARATION);

            // Créer les lignes de commande sans déduire le stock
            for (ProductOrderDTO lineDTO : productOrders) {
                Product product = productRepository.findById(lineDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", lineDTO.getProductId()));

                ProductOrder productOrder = productOrderMapper.toEntity(lineDTO);
                productOrder.setOrder(savedOrder);
                productOrder.setProduct(product);

                BigDecimal lineTotal = lineDTO.getUnitPrice().multiply(BigDecimal.valueOf(lineDTO.getQuantity()));
                productOrder.setTotalPrice(lineTotal);

                orderLines.add(productOrder);
            }
        }

        // Sauvegarder toutes les lignes via le repository
        productOrderRepository.saveAll(orderLines);
        savedOrder.setProductOrders(orderLines);
        Order finalOrder = orderRepository.save(savedOrder);

        return orderMapper.toDTO(finalOrder);
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

        // Vérifier le stock pour tous les produits
        boolean allProductsInStock = true;
        List<ProductOrderDTO> productOrders = orderDTO.getProductOrders();

        for (ProductOrderDTO lineDTO : productOrders) {
            Product product = productRepository.findById(lineDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", lineDTO.getProductId()));

            if (product.getStock() < lineDTO.getQuantity()) {
                allProductsInStock = false;
                break;
            }
        }

        // Supprimer les anciennes lignes de commande via orphanRemoval (vider la collection)
        existingOrder.getProductOrders().clear();

        // Déterminer le statut et gérer le stock
        if (allProductsInStock) {
            existingOrder.setStatus(OrderStatus.EN_ROUTE);

            // Déduire le stock pour chaque produit
            for (ProductOrderDTO lineDTO : productOrders) {
                Product product = productRepository.findById(lineDTO.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product", "id", lineDTO.getProductId()));

                // Déduire le stock
                int newStock = product.getStock() - lineDTO.getQuantity();
                product.setStock(newStock);
                productRepository.save(product);
            }
        } else {
            existingOrder.setStatus(OrderStatus.EN_PREPARATION);
        }

        // Créer les nouvelles lignes de commande
        List<ProductOrder> newOrderLines = new ArrayList<>();
        for (ProductOrderDTO lineDTO : productOrders) {
            Product product = productRepository.findById(lineDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", lineDTO.getProductId()));

            ProductOrder productOrder = productOrderMapper.toEntity(lineDTO);
            productOrder.setOrder(existingOrder);
            productOrder.setProduct(product);

            BigDecimal lineTotal = lineDTO.getUnitPrice().multiply(BigDecimal.valueOf(lineDTO.getQuantity()));
            productOrder.setTotalPrice(lineTotal);

            newOrderLines.add(productOrder);
        }

        // Ajouter les nouvelles lignes à la collection
        existingOrder.getProductOrders().addAll(newOrderLines);
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

        // Si la commande était EN_ROUTE, remettre le stock
        if (order.getStatus() == OrderStatus.EN_ROUTE) {
            for (ProductOrder productOrder : order.getProductOrders()) {
                Product product = productOrder.getProduct();
                product.setStock(product.getStock() + productOrder.getQuantity());
                productRepository.save(product);
            }
        }

        orderRepository.deleteById(id);
    }

    // Les autres méthodes restent inchangées...
    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(orderMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getOrdersByStatus(OrderStatus status, Pageable pageable) {
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