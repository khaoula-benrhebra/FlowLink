package org.supplychain.supplychain.service.Production.ProductionOrder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.supplychain.supplychain.dto.Production.ProductionOrderDTO;
import org.supplychain.supplychain.enums.Priority;
import org.supplychain.supplychain.enums.ProductionOrderStatus;
import org.supplychain.supplychain.exception.InsufficientStockException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Production.ProductionOrderMapper;
import org.supplychain.supplychain.model.BillOfMaterial;
import org.supplychain.supplychain.model.Product;
import org.supplychain.supplychain.model.ProductionOrder;
import org.supplychain.supplychain.model.RawMaterial;
import org.supplychain.supplychain.repository.Production.ProductRepository;
import org.supplychain.supplychain.repository.Production.ProductionOrderRepository;
import org.supplychain.supplychain.repository.approvisionnement.RawMaterialRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class ProductionOrderServiceImplTest {

    @Mock
    private ProductionOrderRepository productionOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductionOrderMapper productionOrderMapper;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private ProductionOrderServiceImpl productionOrderService;

    private Product product;
    private RawMaterial bois;
    private RawMaterial vis;
    private ProductionOrderDTO orderDTO;
    private ProductionOrder productionOrder;

    @BeforeEach
    void setUp() {
        // Créer les matières premières
        bois = new RawMaterial();
        bois.setIdMaterial(1L);
        bois.setName("Bois");
        bois.setStock(100);
        bois.setUnit("planches");

        vis = new RawMaterial();
        vis.setIdMaterial(2L);
        vis.setName("Vis");
        vis.setStock(200);
        vis.setUnit("unités");

        // Créer le BOM (Bill of Materials)
        BillOfMaterial bomBois = new BillOfMaterial();
        bomBois.setMaterial(bois);
        bomBois.setQuantity(4); // 4 planches par chaise

        BillOfMaterial bomVis = new BillOfMaterial();
        bomVis.setMaterial(vis);
        bomVis.setQuantity(10); // 10 vis par chaise

        List<BillOfMaterial> bomList = new ArrayList<>();
        bomList.add(bomBois);
        bomList.add(bomVis);

        // Créer le produit
        product = new Product();
        product.setIdProduct(1L);
        product.setName("Chaise");
        product.setStock(5); // 5 chaises en stock
        product.setProductionTime(2); // 2 jours par unité
        product.setCost(BigDecimal.valueOf(50));
        product.setBillOfMaterials(bomList);

        // Créer le DTO de commande
        orderDTO = new ProductionOrderDTO();
        orderDTO.setProductId(1L);
        orderDTO.setQuantity(3); // Commander 3 chaises
        orderDTO.setStartDate(LocalDate.now());

        // Créer l'objet ProductionOrder pour le mapping
        productionOrder = new ProductionOrder();
        productionOrder.setIdOrder(1L);
        productionOrder.setQuantity(3);
        productionOrder.setStartDate(LocalDate.now());
    }

    // ==================== CAS 1 : STOCK PRODUIT SUFFISANT ====================

    @Test
    void testCreateProductionOrder_StockProduitSuffisant_ShouldReturnTermine() {
        // Given : Stock produit (5) >= quantité demandée (3)
        orderDTO.setQuantity(3);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(orderDTO)).thenReturn(productionOrder);
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder);
        when(productionOrderMapper.toDTO(productionOrder)).thenReturn(orderDTO);

        // When
        ProductionOrderDTO result = productionOrderService.createProductionOrder(orderDTO);

        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).save(product); // Stock produit déduit
        assertEquals(2, product.getStock()); // 5 - 3 = 2
        verify(rawMaterialRepository, never()).save(any()); // Pas de déduction matières premières
    }

    // ==================== CAS 2 : STOCK PRODUIT INSUFFISANT ====================

    @Test
    void testCreateProductionOrder_StockMatieresSuffisant_ShouldReturnEnProduction() {
        // Given : Stock produit (5) < quantité demandée (10)
        // Donc il faut produire : 10 - 5 = 5 chaises
        // Matières nécessaires : Bois = 5 * 4 = 20, Vis = 5 * 10 = 50
        // Stock disponible : Bois = 100, Vis = 200 (suffisant)
        orderDTO.setQuantity(10);
        productionOrder.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(orderDTO)).thenReturn(productionOrder);
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder);
        when(productionOrderMapper.toDTO(productionOrder)).thenReturn(orderDTO);

        // When
        ProductionOrderDTO result = productionOrderService.createProductionOrder(orderDTO);

        // Then
        assertNotNull(result);

        // Vérifier la déduction des stocks de matières premières
        assertEquals(80, bois.getStock()); // 100 - 20 = 80
        assertEquals(150, vis.getStock()); // 200 - 50 = 150

        verify(rawMaterialRepository, times(2)).save(any(RawMaterial.class));
        verify(productionOrderRepository, times(1)).save(any(ProductionOrder.class));
    }

    @Test
    void testCreateProductionOrder_StockMatieresInsuffisant_ShouldThrowException() {
        // Given : Stock produit (5) < quantité demandée (30)
        // Donc il faut produire : 30 - 5 = 25 chaises
        // Matières nécessaires : Bois = 25 * 4 = 100, Vis = 25 * 10 = 250
        // Stock disponible : Bois = 100 (juste suffisant), Vis = 200 (insuffisant)
        orderDTO.setQuantity(30);
        productionOrder.setQuantity(30);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(orderDTO)).thenReturn(productionOrder);
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder);

        // When & Then
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> productionOrderService.createProductionOrder(orderDTO)
        );

        // Vérifier le message d'erreur
        assertTrue(exception.getMessage().contains("Stock insuffisant"));
        assertTrue(exception.getMessage().contains("Vis"));

        // Vérifier que l'ordre est quand même créé avec statut EN_ATTENTE
        verify(productionOrderRepository, times(1)).save(any(ProductionOrder.class));

        // Vérifier qu'AUCUN stock n'a été déduit
        assertEquals(100, bois.getStock());
        assertEquals(200, vis.getStock());
        verify(rawMaterialRepository, never()).save(any(RawMaterial.class));
    }

    @Test
    void testCreateProductionOrder_StockBoisInsuffisant_ShouldThrowException() {
        // Given : Seulement le bois est insuffisant
        bois.setStock(15); // Insuffisant pour produire 5 chaises (besoin de 20)
        orderDTO.setQuantity(10);
        productionOrder.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(orderDTO)).thenReturn(productionOrder);
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder);

        // When & Then
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> productionOrderService.createProductionOrder(orderDTO)
        );

        assertTrue(exception.getMessage().contains("Bois"));
        assertTrue(exception.getMessage().contains("Nécessaire: 20"));
        assertTrue(exception.getMessage().contains("Disponible: 15"));
    }

    // ==================== CAS 3 : PRODUIT SANS BOM ====================

    @Test
    void testCreateProductionOrder_ProduitSansBOM_ShouldThrowException() {
        // Given : Produit sans BOM
        product.setBillOfMaterials(new ArrayList<>());
        orderDTO.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(orderDTO)).thenReturn(productionOrder);

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> productionOrderService.createProductionOrder(orderDTO)
        );

        assertTrue(exception.getMessage().contains("nomenclature"));
    }

    // ==================== CAS 4 : PRODUIT INTROUVABLE ====================

    @Test
    void testCreateProductionOrder_ProduitIntrouvable_ShouldThrowException() {
        // Given : Produit n'existe pas
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productionOrderService.createProductionOrder(orderDTO)
        );

        assertTrue(exception.getMessage().contains("Produit non trouvé"));
    }

    // ==================== CAS 5 : STOCK NULL ====================

    @Test
    void testCreateProductionOrder_StockProduitNull_ShouldTreatAsZero() {
        // Given : Stock du produit est null
        product.setStock(null);
        orderDTO.setQuantity(5);
        productionOrder.setQuantity(5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(orderDTO)).thenReturn(productionOrder);
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder);
        when(productionOrderMapper.toDTO(productionOrder)).thenReturn(orderDTO);

        // When
        ProductionOrderDTO result = productionOrderService.createProductionOrder(orderDTO);

        // Then
        assertNotNull(result);

        // Le stock étant null (= 0), il faut produire les 5 chaises
        // Matières nécessaires : Bois = 5 * 4 = 20, Vis = 5 * 10 = 50
        assertEquals(80, bois.getStock()); // 100 - 20 = 80
        assertEquals(150, vis.getStock()); // 200 - 50 = 150
    }

    @Test
    void testCreateProductionOrder_StockMatiereNull_ShouldTreatAsZero() {
        // Given : Stock de la matière première est null
        bois.setStock(null);
        orderDTO.setQuantity(10);
        productionOrder.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(orderDTO)).thenReturn(productionOrder);
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder);

        // When & Then
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> productionOrderService.createProductionOrder(orderDTO)
        );

        assertTrue(exception.getMessage().contains("Bois"));
        assertTrue(exception.getMessage().contains("Disponible: 0"));
    }

    // ==================== CAS 6 : CALCUL DES DATES ====================

    @Test
    void testCreateProductionOrder_VerifierCalculDates() {
        // Given : Production de 5 chaises (10 demandées - 5 en stock)
        // Temps de production = 2 jours par chaise
        // Total = 5 * 2 = 10 jours
        orderDTO.setQuantity(10);
        orderDTO.setStartDate(LocalDate.of(2025, 1, 1));
        productionOrder.setQuantity(10);
        productionOrder.setStartDate(LocalDate.of(2025, 1, 1));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(orderDTO)).thenReturn(productionOrder);
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder);
        when(productionOrderMapper.toDTO(productionOrder)).thenReturn(orderDTO);

        // When
        productionOrderService.createProductionOrder(orderDTO);

        // Then : La date de fin devrait être 10 jours après le début
        assertEquals(LocalDate.of(2025, 1, 11), productionOrder.getEndDate());
    }

    // ==================== CAS 7 : PRIORITÉ PAR DÉFAUT ====================

    @Test
    void testCreateProductionOrder_PrioriteParDefaut_ShouldBeStandard() {
        // Given : Pas de priorité définie
        orderDTO.setQuantity(3);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productionOrderMapper.toEntity(orderDTO)).thenReturn(productionOrder);
        when(productionOrderRepository.save(any(ProductionOrder.class))).thenReturn(productionOrder);
        when(productionOrderMapper.toDTO(productionOrder)).thenReturn(orderDTO);

        // When
        productionOrderService.createProductionOrder(orderDTO);

        // Then
        assertEquals(Priority.STANDARD, productionOrder.getPriority());
    }
}