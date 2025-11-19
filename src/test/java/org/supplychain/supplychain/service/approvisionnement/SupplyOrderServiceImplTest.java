package org.supplychain.supplychain.service.approvisionnement;

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
import org.supplychain.supplychain.dto.Approvisionnement.SupplyOrderDTO;
import org.supplychain.supplychain.dto.Approvisionnement.SupplyOrderLineDTO;
import org.supplychain.supplychain.enums.SupplyOrderStatus;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Approvisionnement.SupplyOrderLineMapper;
import org.supplychain.supplychain.mapper.Approvisionnement.SupplyOrderMapper;
import org.supplychain.supplychain.model.RawMaterial;
import org.supplychain.supplychain.model.Supplier;
import org.supplychain.supplychain.model.SupplyOrder;
import org.supplychain.supplychain.model.SupplyOrderLine;
import org.supplychain.supplychain.repository.approvisionnement.RawMaterialRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplierRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplyOrderLineRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplyOrderRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplyOrderServiceImplTest {

    @Mock
    private SupplyOrderRepository supplyOrderRepository;

    @Mock
    private SupplyOrderLineRepository supplyOrderLineRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @Mock
    private SupplyOrderMapper supplyOrderMapper;

    @Mock
    private SupplyOrderLineMapper supplyOrderLineMapper;

    @InjectMocks
    private SupplyOrderServiceImpl supplyOrderService;

    private SupplyOrderDTO supplyOrderDTO;
    private SupplyOrder supplyOrder;
    private Supplier supplier;
    private RawMaterial rawMaterial;
    private SupplyOrderLineDTO orderLineDTO;
    private final Long orderId = 1L;
    private final Long supplierId = 1L;
    private final Long materialId = 1L;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setIdSupplier(supplierId);
        supplier.setName("Test Supplier");

        rawMaterial = new RawMaterial();
        rawMaterial.setIdMaterial(materialId);
        rawMaterial.setName("Test Material");
        rawMaterial.setStock(100);

        supplyOrder = new SupplyOrder();
        supplyOrder.setIdOrder(orderId);
        supplyOrder.setSupplier(supplier);
        supplyOrder.setStatus(SupplyOrderStatus.EN_ATTENTE);
        supplyOrder.setTotalAmount(BigDecimal.valueOf(500.0));

        orderLineDTO = new SupplyOrderLineDTO();
        orderLineDTO.setRawMaterialId(materialId);
        orderLineDTO.setQuantity(5);
        orderLineDTO.setUnitPrice(BigDecimal.valueOf(100.0));

        supplyOrderDTO = new SupplyOrderDTO();
        supplyOrderDTO.setSupplierId(supplierId);
        supplyOrderDTO.setOrderDate(LocalDate.now());
        supplyOrderDTO.setStatus(SupplyOrderStatus.EN_ATTENTE);
        supplyOrderDTO.setOrderLines(new ArrayList<>(Collections.singletonList(orderLineDTO)));
    }

    @Test
    void createSupplyOrder_Success() {
        // Given: un nouveau bon de commande avec un fournisseur et des lignes valides
        SupplyOrderLine orderLine = new SupplyOrderLine();
        orderLine.setRawMaterial(rawMaterial);
        orderLine.setQuantity(5);
        orderLine.setUnitPrice(BigDecimal.valueOf(100.0));
        
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(supplyOrderMapper.toEntity(supplyOrderDTO)).thenReturn(supplyOrder);
        when(supplyOrderRepository.save(supplyOrder)).thenReturn(supplyOrder);
        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.of(rawMaterial));
        when(supplyOrderLineMapper.toEntity(orderLineDTO)).thenReturn(orderLine);
        when(supplyOrderLineRepository.saveAll(any())).thenReturn(new ArrayList<>());
        supplyOrder.setOrderLines(new ArrayList<>());
        when(supplyOrderMapper.toDTO(supplyOrder)).thenReturn(supplyOrderDTO);
        when(supplyOrderLineMapper.toDTOList(supplyOrder.getOrderLines())).thenReturn(new ArrayList<>(Collections.singletonList(orderLineDTO)));

        // When: on crée un nouveau bon de commande
        SupplyOrderDTO result = supplyOrderService.createSupplyOrder(supplyOrderDTO);

        // Then: le bon de commande est correctement créé
        assertNotNull(result);
        verify(supplierRepository).findById(supplierId);
        verify(rawMaterialRepository).findById(materialId);
        verify(supplyOrderLineRepository).saveAll(any());
    }

    @Test
    void createSupplyOrder_SupplierNotFound() {
        // Given: un ID de fournisseur qui n'existe pas
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        // When: on essaie de créer un bon de commande avec un fournisseur inexistant
        // Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class, () -> supplyOrderService.createSupplyOrder(supplyOrderDTO));
        verify(supplierRepository).findById(supplierId);
    }

    @Test
    void createSupplyOrder_MaterialNotFound() {
        // Given: un ID de matière première qui n'existe pas
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(supplyOrderMapper.toEntity(supplyOrderDTO)).thenReturn(supplyOrder);
        when(supplyOrderRepository.save(supplyOrder)).thenReturn(supplyOrder);
        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.empty());

        // When: on essaie de créer un bon de commande avec une matière première inexistante
        // Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class, () -> supplyOrderService.createSupplyOrder(supplyOrderDTO));
        verify(rawMaterialRepository).findById(materialId);
    }

    @Test
    void updateSupplyOrder_Success() {
        // Given: un bon de commande existant à mettre à jour
        SupplyOrderDTO updatedDTO = new SupplyOrderDTO();
        updatedDTO.setSupplierId(supplierId);
        updatedDTO.setOrderDate(LocalDate.now());
        updatedDTO.setStatus(SupplyOrderStatus.EN_COURS);
        updatedDTO.setOrderLines(new ArrayList<>(Collections.singletonList(orderLineDTO)));

        SupplyOrderLine orderLine = new SupplyOrderLine();
        orderLine.setRawMaterial(rawMaterial);
        orderLine.setQuantity(5);
        orderLine.setUnitPrice(BigDecimal.valueOf(100.0));

        supplyOrder.setOrderLines(new ArrayList<>());

        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.of(supplyOrder));
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.of(rawMaterial));
        when(supplyOrderLineMapper.toEntity(orderLineDTO)).thenReturn(orderLine);
        when(supplyOrderRepository.save(supplyOrder)).thenReturn(supplyOrder);
        when(supplyOrderMapper.toDTO(supplyOrder)).thenReturn(updatedDTO);
        when(supplyOrderLineMapper.toDTOList(supplyOrder.getOrderLines())).thenReturn(new ArrayList<>(Collections.singletonList(orderLineDTO)));

        // When: on met à jour le bon de commande
        SupplyOrderDTO result = supplyOrderService.updateSupplyOrder(orderId, updatedDTO);

        // Then: le bon de commande est correctement mis à jour
        assertNotNull(result);
        verify(supplyOrderRepository).findById(orderId);
        verify(supplierRepository).findById(supplierId);
    }

    @Test
    void updateSupplyOrder_NotFound() {
        // Given: un ID qui n'existe pas
        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When: on essaie de mettre à jour ce bon de commande inexistant
        // Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class, () -> supplyOrderService.updateSupplyOrder(orderId, supplyOrderDTO));
        verify(supplyOrderRepository).findById(orderId);
    }

    @Test
    void deleteSupplyOrder_Success() {
        // Given: un bon de commande avec un statut qui permet la suppression
        supplyOrder.setStatus(SupplyOrderStatus.EN_ATTENTE);
        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.of(supplyOrder));
        doNothing().when(supplyOrderRepository).deleteById(orderId);

        // When: on supprime le bon de commande
        assertDoesNotThrow(() -> supplyOrderService.deleteSupplyOrder(orderId));
        
        // Then: la suppression est réussie
        verify(supplyOrderRepository).findById(orderId);
        verify(supplyOrderRepository).deleteById(orderId);
    }

    @Test
    void deleteSupplyOrder_NotFound() {
        // Given: un ID qui n'existe pas
        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When: on essaie de supprimer ce bon de commande inexistant
        // Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class, () -> supplyOrderService.deleteSupplyOrder(orderId));
        verify(supplyOrderRepository).findById(orderId);
    }

    @Test
    void deleteSupplyOrder_Received() {
        // Given: un bon de commande avec un statut RECUE (reçu)
        supplyOrder.setStatus(SupplyOrderStatus.RECUE);
        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.of(supplyOrder));

        // When: on essaie de supprimer ce bon de commande reçu
        // Then: une exception ResourceInUseException est levée (protection des données)
        assertThrows(ResourceInUseException.class, () -> supplyOrderService.deleteSupplyOrder(orderId));
        verify(supplyOrderRepository).findById(orderId);
        verify(supplyOrderRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllSupplyOrders_Success() {
        // Given: une liste paginée de bons de commande
        Pageable pageable = PageRequest.of(0, 10);
        List<SupplyOrder> orderList = Collections.singletonList(supplyOrder);
        Page<SupplyOrder> orderPage = new PageImpl<>(orderList, pageable, 1);

        when(supplyOrderRepository.findAll(pageable)).thenReturn(orderPage);
        when(supplyOrderMapper.toDTO(supplyOrder)).thenReturn(supplyOrderDTO);
        when(supplyOrderLineMapper.toDTOList(supplyOrder.getOrderLines())).thenReturn(new ArrayList<>());

        // When: on récupère tous les bons de commande
        Page<SupplyOrderDTO> result = supplyOrderService.getAllSupplyOrders(pageable);

        // Then: la liste est correctement retournée avec les bonnes données
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(supplyOrderRepository).findAll(pageable);
    }

    @Test
    void getSupplyOrdersByStatus_Success() {
        // Given: une liste paginée de bons de commande filtrée par statut
        Pageable pageable = PageRequest.of(0, 10);
        List<SupplyOrder> orderList = Collections.singletonList(supplyOrder);
        Page<SupplyOrder> orderPage = new PageImpl<>(orderList, pageable, 1);

        when(supplyOrderRepository.findByStatus(SupplyOrderStatus.EN_ATTENTE, pageable)).thenReturn(orderPage);
        when(supplyOrderMapper.toDTO(supplyOrder)).thenReturn(supplyOrderDTO);
        when(supplyOrderLineMapper.toDTOList(supplyOrder.getOrderLines())).thenReturn(new ArrayList<>());

        // When: on récupère les bons de commande filtrés par statut
        Page<SupplyOrderDTO> result = supplyOrderService.getSupplyOrdersByStatus(SupplyOrderStatus.EN_ATTENTE, pageable);

        // Then: les résultats filtrés sont correctement retournés
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(supplyOrderRepository).findByStatus(SupplyOrderStatus.EN_ATTENTE, pageable);
    }

    @Test
    void getSupplyOrderById_Success() {
        // Given: un ID de bon de commande qui existe
        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.of(supplyOrder));
        when(supplyOrderMapper.toDTO(supplyOrder)).thenReturn(supplyOrderDTO);
        when(supplyOrderLineMapper.toDTOList(supplyOrder.getOrderLines())).thenReturn(new ArrayList<>());

        // When: on récupère le bon de commande par son ID
        SupplyOrderDTO result = supplyOrderService.getSupplyOrderById(orderId);

        // Then: le bon de commande est correctement retourné
        assertNotNull(result);
        verify(supplyOrderRepository).findById(orderId);
    }

    @Test
    void getSupplyOrderById_NotFound() {
        // Given: un ID qui n'existe pas en base de données
        when(supplyOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When: on essaie de récupérer ce bon de commande inexistant
        // Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class, () -> supplyOrderService.getSupplyOrderById(orderId));
        verify(supplyOrderRepository).findById(orderId);
    }

    @Test
    void getOrderLines_Success() {
        // Given: un bon de commande qui existe avec des lignes
        List<SupplyOrderLine> orderLines = new ArrayList<>();
        when(supplyOrderRepository.existsById(orderId)).thenReturn(true);
        when(supplyOrderLineRepository.findBySupplyOrder_IdOrder(orderId)).thenReturn(orderLines);
        when(supplyOrderLineMapper.toDTOList(orderLines)).thenReturn(new ArrayList<>());

        // When: on récupère les lignes du bon de commande
        List<SupplyOrderLineDTO> result = supplyOrderService.getOrderLines(orderId);

        // Then: les lignes sont correctement retournées
        assertNotNull(result);
        verify(supplyOrderRepository).existsById(orderId);
        verify(supplyOrderLineRepository).findBySupplyOrder_IdOrder(orderId);
    }

    @Test
    void getOrderLines_OrderNotFound() {
        // Given: un ID de bon de commande qui n'existe pas
        when(supplyOrderRepository.existsById(orderId)).thenReturn(false);

        // When: on essaie de récupérer les lignes d'un bon de commande inexistant
        // Then: une exception ResourceNotFoundException est levée
        assertThrows(ResourceNotFoundException.class, () -> supplyOrderService.getOrderLines(orderId));
        verify(supplyOrderRepository).existsById(orderId);
    }
}
