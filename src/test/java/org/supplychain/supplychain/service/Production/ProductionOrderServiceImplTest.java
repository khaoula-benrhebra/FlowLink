package org.supplychain.supplychain.service.Production;

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
import org.supplychain.supplychain.dto.Production.ProductionOrderDTO;
import org.supplychain.supplychain.enums.Priority;
import org.supplychain.supplychain.enums.ProductionOrderStatus;
import org.supplychain.supplychain.exception.InsufficientStockException;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Production.ProductionOrderMapper;
import org.supplychain.supplychain.model.BillOfMaterial;
import org.supplychain.supplychain.model.Product;
import org.supplychain.supplychain.model.ProductionOrder;
import org.supplychain.supplychain.model.RawMaterial;
import org.supplychain.supplychain.repository.Production.ProductRepository;
import org.supplychain.supplychain.repository.Production.ProductionOrderRepository;
import org.supplychain.supplychain.repository.approvisionnement.RawMaterialRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductionOrderServiceImplTest {

    @Mock
    private ProductionOrderRepository productionOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private ProductionOrderMapper productionOrderMapper;

    @InjectMocks
    private ProductionOrderServiceImpl productionOrderService;

    private ProductionOrderDTO productionOrderDTO;
    private ProductionOrder productionOrder;
    private Product product;
    private RawMaterial rawMaterial;
    private BillOfMaterial billOfMaterial;

    private final Long orderId = 1L;
    private final Long productId = 1L;
    private final Long materialId = 1L;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setIdProduct(productId);
        product.setName("Test Product");
        product.setStock(100);
        product.setProductionTime(2);
        product.setBillOfMaterials(new ArrayList<>());

        rawMaterial = new RawMaterial();
        rawMaterial.setIdMaterial(materialId);
        rawMaterial.setName("Test Material");
        rawMaterial.setStock(500);
        rawMaterial.setUnit("kg");

        billOfMaterial = new BillOfMaterial();
        billOfMaterial.setMaterial(rawMaterial);
        billOfMaterial.setQuantity(5);
        product.getBillOfMaterials().add(billOfMaterial);

        productionOrder = new ProductionOrder();
        productionOrder.setIdOrder(orderId);
        productionOrder.setProduct(product);
        productionOrder.setQuantity(50);
        productionOrder.setStatus(ProductionOrderStatus.EN_PRODUCTION);
        productionOrder.setPriority(Priority.STANDARD);
        productionOrder.setStartDate(LocalDate.now());
        productionOrder.setEndDate(LocalDate.now().plusDays(4));

        productionOrderDTO = new ProductionOrderDTO();
        productionOrderDTO.setIdOrder(orderId);
        productionOrderDTO.setProductId(productId);
        productionOrderDTO.setProductName("Test Product");
        productionOrderDTO.setQuantity(50);
        productionOrderDTO.setStatus(ProductionOrderStatus.EN_PRODUCTION);
        productionOrderDTO.setPriority(Priority.STANDARD);
        productionOrderDTO.setStartDate(LocalDate.now());
    }

    @Test
    void createProductionOrder_SufficientStock() {
        // Given: un produit avec stock suffisant (stock >= demande)
        productionOrderDTO.setQuantity(50);
        product.setStock(100);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(productionOrderDTO)).thenReturn(productionOrder);
        productionOrder.setStatus(ProductionOrderStatus.TERMINE);
        when(productionOrderRepository.save(productionOrder)).thenReturn(productionOrder);
        when(productionOrderMapper.toDTO(productionOrder)).thenReturn(productionOrderDTO);

        // When: on crée un ordre de production
        ProductionOrderDTO result = productionOrderService.createProductionOrder(productionOrderDTO);

        // Then: l'ordre est créé avec statut TERMINE et le stock du produit est déduit
        assertNotNull(result);
        assertEquals(ProductionOrderStatus.TERMINE, productionOrder.getStatus());
        verify(productRepository).findById(productId);
        verify(productionOrderRepository).save(productionOrder);
    }

    @Test
    void createProductionOrder_InsufficientStock_WithBOM() {
        // Given: un produit avec stock insuffisant et une BOM qui manque de matières premières
        productionOrderDTO.setQuantity(200);
        product.setStock(50);
        rawMaterial.setStock(10); // Insuffisant pour 150 * 5 = 750 kg

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(productionOrderDTO)).thenReturn(productionOrder);
        productionOrder.setStatus(ProductionOrderStatus.EN_ATTENTE);
        productionOrder.setQuantity(200);
        when(productionOrderRepository.save(productionOrder)).thenReturn(productionOrder);

        // When/Then: une exception InsufficientStockException est levée
        assertThrows(InsufficientStockException.class, 
            () -> productionOrderService.createProductionOrder(productionOrderDTO));
        
        verify(productRepository).findById(productId);
        verify(productionOrderRepository).save(any(ProductionOrder.class));
    }

    @Test
    void createProductionOrder_ProductNotFound() {
        // Given: un ID de produit qui n'existe pas
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When/Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class,
            () -> productionOrderService.createProductionOrder(productionOrderDTO));

        verify(productRepository).findById(productId);
    }

    @Test
    void createProductionOrder_NoBOM() {
        // Given: un produit sans nomenclature (BOM vide)
        product.setBillOfMaterials(new ArrayList<>());
        productionOrderDTO.setQuantity(200);
        product.setStock(50);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(productionOrderDTO)).thenReturn(productionOrder);

        // When/Then: une exception IllegalStateException est levée
        assertThrows(IllegalStateException.class,
            () -> productionOrderService.createProductionOrder(productionOrderDTO));

        verify(productRepository).findById(productId);
    }

    @Test
    void updateProductionOrder_Success() {
        // Given: un ordre de production existant à mettre à jour
        ProductionOrderDTO updatedDTO = new ProductionOrderDTO();
        updatedDTO.setProductId(productId);
        updatedDTO.setQuantity(75);
        updatedDTO.setStatus(ProductionOrderStatus.EN_PRODUCTION);
        updatedDTO.setPriority(Priority.HIGH);
        updatedDTO.setStartDate(LocalDate.now());

        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.of(productionOrder));
        doNothing().when(productionOrderMapper).updateEntityFromDTO(updatedDTO, productionOrder);
        when(productionOrderRepository.save(productionOrder)).thenReturn(productionOrder);
        when(productionOrderMapper.toDTO(productionOrder)).thenReturn(productionOrderDTO);

        // When: on met à jour l'ordre
        ProductionOrderDTO result = productionOrderService.updateProductionOrder(orderId, updatedDTO);

        // Then: l'ordre est correctement mis à jour
        assertNotNull(result);
        verify(productionOrderRepository).findById(orderId);
        verify(productionOrderRepository).save(productionOrder);
    }

    @Test
    void updateProductionOrder_NotFound() {
        // Given: un ID qui n'existe pas
        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When/Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class,
            () -> productionOrderService.updateProductionOrder(orderId, productionOrderDTO));

        verify(productionOrderRepository).findById(orderId);
    }

    @Test
    void cancelProductionOrder_Success() {
        // Given: un ordre en attente qui peut être annulé
        productionOrder.setStatus(ProductionOrderStatus.EN_ATTENTE);
        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.of(productionOrder));
        doNothing().when(productionOrderRepository).delete(productionOrder);

        // When: on annule l'ordre
        assertDoesNotThrow(() -> productionOrderService.cancelProductionOrder(orderId));

        // Then: l'ordre est supprimé
        verify(productionOrderRepository).findById(orderId);
        verify(productionOrderRepository).delete(productionOrder);
    }

    @Test
    void cancelProductionOrder_NotFound() {
        // Given: un ID qui n'existe pas
        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When/Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class,
            () -> productionOrderService.cancelProductionOrder(orderId));

        verify(productionOrderRepository).findById(orderId);
    }

    @Test
    void cancelProductionOrder_InvalidStatus() {
        // Given: un ordre avec un statut qui n'est pas EN_ATTENTE
        productionOrder.setStatus(ProductionOrderStatus.EN_PRODUCTION);
        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.of(productionOrder));

        // When/Then: une exception ResourceInUseException est levée
        assertThrows(ResourceInUseException.class,
            () -> productionOrderService.cancelProductionOrder(orderId));

        verify(productionOrderRepository).findById(orderId);
        verify(productionOrderRepository, never()).delete(any());
    }

    @Test
    void getAllProductionOrders_Success() {
        // Given: une liste paginée d'ordres de production
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductionOrder> orderList = Collections.singletonList(productionOrder);
        Page<ProductionOrder> orderPage = new PageImpl<>(orderList, pageable, 1);

        when(productionOrderRepository.findAll(pageable)).thenReturn(orderPage);
        when(productionOrderMapper.toDTO(productionOrder)).thenReturn(productionOrderDTO);

        // When: on récupère tous les ordres
        Page<ProductionOrderDTO> result = productionOrderService.getAllProductionOrders(pageable);

        // Then: la liste est correctement retournée
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productionOrderRepository).findAll(pageable);
    }

    @Test
    void getProductionOrdersByStatus_Success() {
        // Given: une liste paginée filtrée par statut
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductionOrder> orderList = Collections.singletonList(productionOrder);
        Page<ProductionOrder> orderPage = new PageImpl<>(orderList, pageable, 1);

        when(productionOrderRepository.findByStatus(ProductionOrderStatus.EN_PRODUCTION, pageable))
                .thenReturn(orderPage);
        when(productionOrderMapper.toDTO(productionOrder)).thenReturn(productionOrderDTO);

        // When: on récupère les ordres filtrés par statut
        Page<ProductionOrderDTO> result = productionOrderService.getProductionOrdersByStatus(
                ProductionOrderStatus.EN_PRODUCTION, pageable);

        // Then: les résultats sont correctement retournés
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productionOrderRepository).findByStatus(ProductionOrderStatus.EN_PRODUCTION, pageable);
    }

    @Test
    void getProductionOrderById_Success() {
        // Given: un ID d'ordre qui existe
        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.of(productionOrder));
        when(productionOrderMapper.toDTO(productionOrder)).thenReturn(productionOrderDTO);

        // When: on récupère l'ordre par son ID
        ProductionOrderDTO result = productionOrderService.getProductionOrderById(orderId);

        // Then: l'ordre est correctement retourné
        assertNotNull(result);
        verify(productionOrderRepository).findById(orderId);
    }

    @Test
    void getProductionOrderById_NotFound() {
        // Given: un ID qui n'existe pas
        when(productionOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When/Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class,
            () -> productionOrderService.getProductionOrderById(orderId));

        verify(productionOrderRepository).findById(orderId);
    }
}
