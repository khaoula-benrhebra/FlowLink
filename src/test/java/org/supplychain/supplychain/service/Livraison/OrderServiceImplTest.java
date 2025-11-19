package org.supplychain.supplychain.service.Livraison;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductOrderRepository productOrderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductOrderMapper productOrderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderDTO orderDTO;
    private Order order;
    private Customer customer;
    private Product product;
    private ProductOrderDTO productOrderDTO;
    private ProductOrder productOrder;

    private final Long orderId = 1L;
    private final Long customerId = 1L;
    private final Long productId = 1L;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setIdCustomer(customerId);
        customer.setName("Test Customer");
        customer.setEmail("customer@test.com");
        customer.setPhone("123456789");

        product = new Product();
        product.setIdProduct(productId);
        product.setName("Test Product");
        product.setStock(100);

        productOrderDTO = new ProductOrderDTO();
        productOrderDTO.setProductId(productId);
        productOrderDTO.setProductName("Test Product");
        productOrderDTO.setQuantity(10);
        productOrderDTO.setUnitPrice(BigDecimal.valueOf(50.0));

        order = new Order();
        order.setIdOrder(orderId);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.EN_ROUTE);
        order.setProductOrders(new ArrayList<>());

        productOrder = new ProductOrder();
        productOrder.setProduct(product);
        productOrder.setQuantity(10);
        productOrder.setUnitPrice(BigDecimal.valueOf(50.0));
        productOrder.setTotalPrice(BigDecimal.valueOf(500.0));
        productOrder.setOrder(order);

        orderDTO = new OrderDTO();
        orderDTO.setCustomerId(customerId);
        orderDTO.setCustomerName("Test Customer");
        orderDTO.setStatus(OrderStatus.EN_ROUTE);
        orderDTO.setProductOrders(new ArrayList<>(Collections.singletonList(productOrderDTO)));
    }

    @Test
    void createOrder_AllProductsInStock() {
        // Given: une commande avec des produits en stock suffisant
        product.setStock(100);
        productOrderDTO.setQuantity(10);
        
        ProductOrder productOrder2 = new ProductOrder();
        productOrder2.setProduct(product);
        productOrder2.setQuantity(10);
        productOrder2.setUnitPrice(BigDecimal.valueOf(50.0));
        productOrder2.setTotalPrice(BigDecimal.valueOf(500.0));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(orderMapper.toEntity(orderDTO)).thenReturn(order);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(order)).thenReturn(order);
        when(productOrderMapper.toEntity(productOrderDTO)).thenReturn(productOrder2);
        when(productOrderRepository.saveAll(any())).thenReturn(new ArrayList<>());
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        // When: on crée la commande
        OrderDTO result = orderService.createOrder(orderDTO);

        // Then: la commande est créée avec les bonnes données
        assertNotNull(result);
        verify(customerRepository).findById(customerId);
        verify(productRepository, atLeastOnce()).findById(productId);
    }

    @Test
    void createOrder_InsufficientStock() {
        // Given: une commande avec des produits en stock insuffisant
        product.setStock(5); // Insuffisant pour demande de 10
        productOrderDTO.setQuantity(10);

        ProductOrder productOrder2 = new ProductOrder();
        productOrder2.setProduct(product);
        productOrder2.setQuantity(10);
        productOrder2.setUnitPrice(BigDecimal.valueOf(50.0));
        productOrder2.setTotalPrice(BigDecimal.valueOf(500.0));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(orderMapper.toEntity(orderDTO)).thenReturn(order);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(order)).thenReturn(order);
        when(productOrderMapper.toEntity(productOrderDTO)).thenReturn(productOrder2);
        when(productOrderRepository.saveAll(any())).thenReturn(new ArrayList<>());
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        // When: on crée la commande
        OrderDTO result = orderService.createOrder(orderDTO);

        // Then: la commande est créée avec les bonnes données (stock insuffisant)
        assertNotNull(result);
        verify(customerRepository).findById(customerId);
        verify(productRepository, atLeastOnce()).findById(productId);
    }

    @Test
    void createOrder_CustomerNotFound() {
        // Given: un ID de client qui n'existe pas
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When/Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class,
            () -> orderService.createOrder(orderDTO));

        verify(customerRepository).findById(customerId);
    }

    @Test
    void createOrder_ProductNotFound() {
        // Given: un ID de produit qui n'existe pas
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(orderMapper.toEntity(orderDTO)).thenReturn(order);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When/Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class,
            () -> orderService.createOrder(orderDTO));

        verify(customerRepository).findById(customerId);
        verify(productRepository).findById(productId);
    }

    @Test
    void updateOrder_Success() {
        // Given: une commande existante en attente à mettre à jour
        order.setStatus(OrderStatus.EN_PREPARATION);
        order.setProductOrders(new ArrayList<>(Collections.singletonList(productOrder)));

        OrderDTO updatedDTO = new OrderDTO();
        updatedDTO.setCustomerId(customerId);
        updatedDTO.setStatus(OrderStatus.EN_ROUTE);
        updatedDTO.setProductOrders(new ArrayList<>(Collections.singletonList(productOrderDTO)));

        product.setStock(100);
        productOrderDTO.setQuantity(15);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productOrderMapper.toEntity(productOrderDTO)).thenReturn(productOrder);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDTO(order)).thenReturn(updatedDTO);

        // When: on met à jour la commande
        OrderDTO result = orderService.updateOrder(orderId, updatedDTO);

        // Then: la commande est correctement mise à jour
        assertNotNull(result);
        verify(orderRepository).findById(orderId);
        verify(customerRepository).findById(customerId);
    }

    @Test
    void updateOrder_AlreadyShipped() {
        // Given: une commande déjà expédiée
        order.setStatus(OrderStatus.EN_ROUTE);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When/Then: une exception ResourceInUseException est levée
        assertThrows(ResourceInUseException.class,
            () -> orderService.updateOrder(orderId, orderDTO));

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrder_AlreadyDelivered() {
        // Given: une commande déjà livrée
        order.setStatus(OrderStatus.LIVREE);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When/Then: une exception ResourceInUseException est levée
        assertThrows(ResourceInUseException.class,
            () -> orderService.updateOrder(orderId, orderDTO));

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrder_NotFound() {
        // Given: un ID qui n'existe pas
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When/Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class,
            () -> orderService.updateOrder(orderId, orderDTO));

        verify(orderRepository).findById(orderId);
    }

    @Test
    void cancelOrder_Success() {
        // Given: une commande en attente qui peut être annulée
        order.setStatus(OrderStatus.EN_PREPARATION);
        order.setProductOrders(new ArrayList<>());
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).deleteById(orderId);

        // When: on annule la commande
        assertDoesNotThrow(() -> orderService.cancelOrder(orderId));

        // Then: la commande est supprimée
        verify(orderRepository).findById(orderId);
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void cancelOrder_NotFound() {
        // Given: un ID qui n'existe pas
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When/Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class,
            () -> orderService.cancelOrder(orderId));

        verify(orderRepository).findById(orderId);
    }

    @Test
    void cancelOrder_AlreadyShipped() {
        // Given: une commande déjà expédiée
        order.setStatus(OrderStatus.EN_ROUTE);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When/Then: une exception ResourceInUseException est levée
        assertThrows(ResourceInUseException.class,
            () -> orderService.cancelOrder(orderId));

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).deleteById(anyLong());
    }

    @Test
    void cancelOrder_AlreadyDelivered() {
        // Given: une commande déjà livrée
        order.setStatus(OrderStatus.LIVREE);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When/Then: une exception ResourceInUseException est levée
        assertThrows(ResourceInUseException.class,
            () -> orderService.cancelOrder(orderId));

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllOrders_Success() {
        // Given: une liste paginée de commandes
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = Collections.singletonList(order);
        Page<Order> orderPage = new PageImpl<>(orderList, pageable, 1);

        when(orderRepository.findAll(pageable)).thenReturn(orderPage);
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        // When: on récupère toutes les commandes
        Page<OrderDTO> result = orderService.getAllOrders(pageable);

        // Then: la liste est correctement retournée
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository).findAll(pageable);
    }

    @Test
    void getOrdersByStatus_Success() {
        // Given: une liste paginée filtrée par statut
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orderList = Collections.singletonList(order);
        Page<Order> orderPage = new PageImpl<>(orderList, pageable, 1);

        when(orderRepository.findOrdersByStatus(OrderStatus.EN_ROUTE, pageable)).thenReturn(orderPage);
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        // When: on récupère les commandes filtrées par statut
        Page<OrderDTO> result = orderService.getOrdersByStatus(OrderStatus.EN_ROUTE, pageable);

        // Then: les résultats sont correctement retournés
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository).findOrdersByStatus(OrderStatus.EN_ROUTE, pageable);
    }

    @Test
    void getOrderById_Success() {
        // Given: un ID de commande qui existe
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toDTO(order)).thenReturn(orderDTO);

        // When: on récupère la commande par son ID
        OrderDTO result = orderService.getOrderById(orderId);

        // Then: la commande est correctement retournée
        assertNotNull(result);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderById_NotFound() {
        // Given: un ID qui n'existe pas
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When/Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class,
            () -> orderService.getOrderById(orderId));

        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderProducts_Success() {
        // Given: une commande qui existe avec des lignes de produits
        List<ProductOrder> productOrderList = new ArrayList<>(Collections.singletonList(productOrder));
        when(orderRepository.existsById(orderId)).thenReturn(true);
        when(productOrderRepository.findByOrder_IdOrder(orderId)).thenReturn(productOrderList);
        when(productOrderMapper.toDTOList(productOrderList)).thenReturn(
                new ArrayList<>(Collections.singletonList(productOrderDTO)));

        // When: on récupère les produits de la commande
        List<ProductOrderDTO> result = orderService.getOrderProducts(orderId);

        // Then: les produits sont correctement retournés
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderRepository).existsById(orderId);
        verify(productOrderRepository).findByOrder_IdOrder(orderId);
    }

    @Test
    void getOrderProducts_OrderNotFound() {
        // Given: un ID de commande qui n'existe pas
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // When/Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class,
            () -> orderService.getOrderProducts(orderId));

        verify(orderRepository).existsById(orderId);
    }
}
