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
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Approvisionnement.SupplierMapper;
import org.supplychain.supplychain.model.Supplier;
import org.supplychain.supplychain.repository.approvisionnement.SupplierRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplyOrderRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

//    @Mock
//    private SupplierRepository supplierRepository;
//
//    @Mock
//    private SupplyOrderRepository supplyOrderRepository;
//
//    @Mock
//    private SupplierMapper supplierMapper;
//
//    @InjectMocks
//    private SupplierServiceImpl supplierService;
//
//    private SupplierDTO supplierDTO;
//    private Supplier supplier;
//    private final Long supplierId = 1L;
//
//    @BeforeEach
//    void setUp() {
//        supplierDTO = new SupplierDTO("Test Supplier", "contact@test.com", "test@supplier.com", "123456789", 4.5, 5);
//        supplier = new Supplier();
//        supplier.setIdSupplier(supplierId);
//        supplier.setName("Test Supplier");
//        supplier.setContact("contact@test.com");
//        supplier.setEmail("test@supplier.com");
//        supplier.setPhone("123456789");
//        supplier.setRating(4.5);
//        supplier.setLeadTime(5);
//    }
//
//    @Test
//    void createSupplier_Success() {
//        // Given: un nouveau fournisseur avec un nom unique
//        when(supplierRepository.findByName(supplierDTO.getName())).thenReturn(Optional.empty());
//        when(supplierMapper.toEntity(supplierDTO)).thenReturn(supplier);
//        when(supplierRepository.save(supplier)).thenReturn(supplier);
//        when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);
//
//        // When: on crée un nouveau fournisseur
//        SupplierDTO result = supplierService.createSupplier(supplierDTO);
//
//        // Then: le fournisseur est correctement créé et retourné
//        assertNotNull(result);
//        assertEquals(supplierDTO.getName(), result.getName());
//        verify(supplierRepository).findByName(supplierDTO.getName());
//        verify(supplierRepository).save(supplier);
//    }
//
//    @Test
//    void createSupplier_DuplicateName() {
//        // Given: un fournisseur qui existe déjà avec le même nom
//        when(supplierRepository.findByName(supplierDTO.getName())).thenReturn(Optional.of(supplier));
//
//        // When: on essaie de créer un fournisseur avec un nom dupliqué
//        // Then: une exception DuplicateResourceException est levée
//        assertThrows(DuplicateResourceException.class, () -> supplierService.createSupplier(supplierDTO));
//        verify(supplierRepository).findByName(supplierDTO.getName());
//        verify(supplierRepository, never()).save(any(Supplier.class));
//    }
//
//    @Test
//    void updateSupplier_Success() {
//        // Given: un fournisseur existant à mettre à jour avec de nouvelles données
//        SupplierDTO updatedDTO = new SupplierDTO("Updated Supplier", "new@contact.com", "new@supplier.com", "987654321", 4.8, 3);
//        Supplier existingSupplier = new Supplier();
//        existingSupplier.setIdSupplier(supplierId);
//        existingSupplier.setName("Old Supplier");
//
//        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(existingSupplier));
//        when(supplierRepository.findByName(updatedDTO.getName())).thenReturn(Optional.empty());
//        doNothing().when(supplierMapper).updateEntityFromDTO(updatedDTO, existingSupplier);
//        when(supplierRepository.save(existingSupplier)).thenReturn(existingSupplier);
//        when(supplierMapper.toDTO(existingSupplier)).thenReturn(updatedDTO);
//
//        // When: on met à jour le fournisseur
//        SupplierDTO result = supplierService.updateSupplier(supplierId, updatedDTO);
//
//        // Then: les informations sont correctement mises à jour
//        assertNotNull(result);
//        assertEquals(updatedDTO.getName(), result.getName());
//        verify(supplierRepository).findById(supplierId);
//        verify(supplierRepository).findByName(updatedDTO.getName());
//        verify(supplierRepository).save(existingSupplier);
//    }
//
//    @Test
//    void updateSupplier_NotFound() {
//        // Given: un ID qui n'existe pas
//        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());
//
//        // When: on essaie de mettre à jour ce fournisseur inexistant
//        // Then: une exception ResourceNotFoundException est levée
//        assertThrows(ResourceNotFoundException.class, () -> supplierService.updateSupplier(supplierId, supplierDTO));
//        verify(supplierRepository).findById(supplierId);
//        verify(supplierRepository, never()).save(any(Supplier.class));
//    }
//
//    @Test
//    void updateSupplier_DuplicateName() {
//        // Given: une tentative de changer le nom avec un nom qui existe déjà pour un autre fournisseur
//        SupplierDTO updatedDTO = new SupplierDTO("Other Supplier", "new@contact.com", "new@supplier.com", "987654321", 4.8, 3);
//        Supplier existingSupplier = new Supplier();
//        existingSupplier.setIdSupplier(supplierId);
//        existingSupplier.setName("Old Supplier");
//
//        Supplier otherSupplier = new Supplier();
//        otherSupplier.setIdSupplier(2L);
//        otherSupplier.setName("Other Supplier");
//
//        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(existingSupplier));
//        when(supplierRepository.findByName(updatedDTO.getName())).thenReturn(Optional.of(otherSupplier));
//
//        // When: on essaie de changer le nom avec un nom qui existe déjà
//        // Then: une exception DuplicateResourceException est levée
//        assertThrows(DuplicateResourceException.class, () -> supplierService.updateSupplier(supplierId, updatedDTO));
//        verify(supplierRepository).findById(supplierId);
//        verify(supplierRepository).findByName(updatedDTO.getName());
//        verify(supplierRepository, never()).save(any(Supplier.class));
//    }
//
//    @Test
//    void deleteSupplier_Success() {
//        // Given: un fournisseur qui existe et n'a pas de commandes actives
//        when(supplierRepository.existsById(supplierId)).thenReturn(true);
//        when(supplyOrderRepository.hasActiveOrders(supplierId)).thenReturn(false);
//        doNothing().when(supplierRepository).deleteById(supplierId);
//
//        // When: on supprime le fournisseur
//        assertDoesNotThrow(() -> supplierService.deleteSupplier(supplierId));
//
//        // Then: la suppression est réussie
//        verify(supplierRepository).existsById(supplierId);
//        verify(supplyOrderRepository).hasActiveOrders(supplierId);
//        verify(supplierRepository).deleteById(supplierId);
//    }
//
//    @Test
//    void deleteSupplier_NotFound() {
//        // Given: un ID qui n'existe pas
//        when(supplierRepository.existsById(supplierId)).thenReturn(false);
//
//        // When: on essaie de supprimer ce fournisseur inexistant
//        // Then: une exception ResourceNotFoundException est levée
//        assertThrows(ResourceNotFoundException.class, () -> supplierService.deleteSupplier(supplierId));
//        verify(supplierRepository).existsById(supplierId);
//        verify(supplyOrderRepository, never()).hasActiveOrders(anyLong());
//        verify(supplierRepository, never()).deleteById(anyLong());
//    }
//
//    @Test
//    void deleteSupplier_HasActiveOrders() {
//        // Given: un fournisseur qui a des commandes actives
//        when(supplierRepository.existsById(supplierId)).thenReturn(true);
//        when(supplyOrderRepository.hasActiveOrders(supplierId)).thenReturn(true);
//
//        // When: on essaie de supprimer ce fournisseur avec des commandes actives
//        // Then: une exception ResourceInUseException est levée (protection des données)
//        assertThrows(ResourceInUseException.class, () -> supplierService.deleteSupplier(supplierId));
//        verify(supplierRepository).existsById(supplierId);
//        verify(supplyOrderRepository).hasActiveOrders(supplierId);
//        verify(supplierRepository, never()).deleteById(anyLong());
//    }
//
//    @Test
//    void getAllSuppliers_Success() {
//        // Given: une liste paginée de fournisseurs
//        Pageable pageable = PageRequest.of(0, 10);
//        List<Supplier> supplierList = Collections.singletonList(supplier);
//        Page<Supplier> supplierPage = new PageImpl<>(supplierList, pageable, 1);
//
//        when(supplierRepository.findAll(pageable)).thenReturn(supplierPage);
//        when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);
//
//        // When: on récupère tous les fournisseurs
//        Page<SupplierDTO> result = supplierService.getAllSuppliers(pageable);
//
//        // Then: la liste est correctement retournée avec les bonnes données
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//        assertEquals(supplierDTO.getName(), result.getContent().get(0).getName());
//        verify(supplierRepository).findAll(pageable);
//    }
//
//    @Test
//    void searchSuppliersByName_Success() {
//        // Given: un terme de recherche et une liste paginée de résultats
//        String searchTerm = "Test";
//        Pageable pageable = PageRequest.of(0, 10);
//        List<Supplier> supplierList = Collections.singletonList(supplier);
//        Page<Supplier> supplierPage = new PageImpl<>(supplierList, pageable, 1);
//
//        when(supplierRepository.findByNameContainingIgnoreCase(searchTerm, pageable)).thenReturn(supplierPage);
//        when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);
//
//        // When: on recherche des fournisseurs par nom
//        Page<SupplierDTO> result = supplierService.searchSuppliersByName(searchTerm, pageable);
//
//        // Then: les résultats de recherche sont correctement retournés
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//        verify(supplierRepository).findByNameContainingIgnoreCase(searchTerm, pageable);
//    }
//
//    @Test
//    void getSupplierById_Success() {
//        // Given: un ID de fournisseur qui existe
//        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
//        when(supplierMapper.toDTO(supplier)).thenReturn(supplierDTO);
//
//        // When: on récupère le fournisseur par son ID
//        SupplierDTO result = supplierService.getSupplierById(supplierId);
//
//        // Then: le fournisseur est correctement retourné
//        assertNotNull(result);
//        assertEquals(supplierDTO.getName(), result.getName());
//        verify(supplierRepository).findById(supplierId);
//    }
//
//    @Test
//    void getSupplierById_NotFound() {
//        // Given: un ID qui n'existe pas en base de données
//        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());
//
//        // When: on essaie de récupérer ce fournisseur inexistant
//        // Then: une exception ResourceNotFoundException est levée
//        assertThrows(ResourceNotFoundException.class, () -> supplierService.getSupplierById(supplierId));
//        verify(supplierRepository).findById(supplierId);
//    }
}
