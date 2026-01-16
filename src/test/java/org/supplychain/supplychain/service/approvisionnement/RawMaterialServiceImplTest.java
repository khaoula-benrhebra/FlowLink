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
import org.supplychain.supplychain.mapper.Approvisionnement.RawMaterialMapper;
import org.supplychain.supplychain.model.RawMaterial;
import org.supplychain.supplychain.repository.approvisionnement.RawMaterialRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplyOrderLineRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RawMaterialServiceImplTest {

//    @Mock
//    private RawMaterialRepository rawMaterialRepository;
//
//    @Mock
//    private SupplyOrderLineRepository supplyOrderLineRepository;
//
//    @Mock
//    private RawMaterialMapper rawMaterialMapper;
//
//    @InjectMocks
//    private RawMaterialServiceImpl rawMaterialService;
//
//    private RawMaterialDTO rawMaterialDTO;
//    private RawMaterial rawMaterial;
//    private final Long materialId = 1L;
//
//    @BeforeEach
//    void setUp() {
//        rawMaterialDTO = new RawMaterialDTO("Test Material", 100, 50, "Unit");
//        rawMaterial = new RawMaterial();
//        rawMaterial.setIdMaterial(materialId);
//        rawMaterial.setName("Test Material");
//        rawMaterial.setStock(100);
//        rawMaterial.setStockMin(50);
//        rawMaterial.setUnit("Unit");
//    }
//
//    @Test
//    void createRawMaterial_Success() {
//        // Given: une matière première avec un nom unique et un mapper qui convertit les DTOs
//        when(rawMaterialRepository.findByName(rawMaterialDTO.getName())).thenReturn(Optional.empty());
//        when(rawMaterialMapper.toEntity(rawMaterialDTO)).thenReturn(rawMaterial);
//        when(rawMaterialRepository.save(rawMaterial)).thenReturn(rawMaterial);
//        when(rawMaterialMapper.toDTO(rawMaterial)).thenReturn(rawMaterialDTO);
//
//        // When: on crée une nouvelle matière première
//        RawMaterialDTO result = rawMaterialService.createRawMaterial(rawMaterialDTO);
//
//        // Then: la matière première est correctement créée et retournée
//        assertNotNull(result);
//        assertEquals(rawMaterialDTO.getName(), result.getName());
//        verify(rawMaterialRepository).findByName(rawMaterialDTO.getName());
//        verify(rawMaterialRepository).save(rawMaterial);
//    }
//
//    @Test
//    void createRawMaterial_DuplicateName() {
//        // Given: une matière première qui existe déjà avec le même nom
//        when(rawMaterialRepository.findByName(rawMaterialDTO.getName())).thenReturn(Optional.of(rawMaterial));
//
//        // When: on essaie de créer une matière première avec un nom dupliqué
//        // Then: une exception DuplicateResourceException est levée
//        assertThrows(DuplicateResourceException.class, () -> rawMaterialService.createRawMaterial(rawMaterialDTO));
//        verify(rawMaterialRepository).findByName(rawMaterialDTO.getName());
//        verify(rawMaterialRepository, never()).save(any(RawMaterial.class));
//    }
//
//    @Test
//    void updateRawMaterial_Success() {
//        // Given: une matière première existante à mettre à jour avec de nouvelles données
//        RawMaterialDTO updatedDTO = new RawMaterialDTO("Updated Material", 200, 100, "Kg");
//        RawMaterial existingMaterial = new RawMaterial();
//        existingMaterial.setIdMaterial(materialId);
//        existingMaterial.setName("Old Material");
//        existingMaterial.setStock(100);
//        existingMaterial.setStockMin(50);
//        existingMaterial.setUnit("Unit");
//
//        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.of(existingMaterial));
//        when(rawMaterialRepository.findByName(updatedDTO.getName())).thenReturn(Optional.empty());
//        doNothing().when(rawMaterialMapper).updateEntityFromDTO(updatedDTO, existingMaterial);
//        when(rawMaterialRepository.save(existingMaterial)).thenReturn(existingMaterial);
//        when(rawMaterialMapper.toDTO(existingMaterial)).thenReturn(updatedDTO);
//
//        // When: on met à jour la matière première
//        RawMaterialDTO result = rawMaterialService.updateRawMaterial(materialId, updatedDTO);
//
//        // Then: les informations sont correctement mises à jour
//        assertNotNull(result);
//        assertEquals(updatedDTO.getName(), result.getName());
//        verify(rawMaterialRepository).findById(materialId);
//        verify(rawMaterialRepository).findByName(updatedDTO.getName());
//        verify(rawMaterialRepository).save(existingMaterial);
//    }
//
//    @Test
//    void updateRawMaterial_NotFound() {
//        // Given: un ID qui n'existe pas en base de données
//        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.empty());
//
//        // When: on essaie de mettre à jour cette matière première inexistante
//        // Then: une exception ResourceNotFoundException est levée
//        assertThrows(ResourceNotFoundException.class, () -> rawMaterialService.updateRawMaterial(materialId, rawMaterialDTO));
//        verify(rawMaterialRepository).findById(materialId);
//        verify(rawMaterialRepository, never()).save(any(RawMaterial.class));
//    }
//
//    @Test
//    void updateRawMaterial_DuplicateName() {
//        // Given: une matière première avec un nom qui existe déjà pour une autre entrée
//        RawMaterialDTO updatedDTO = new RawMaterialDTO("Other Material", 200, 100, "Kg");
//        RawMaterial existingMaterial = new RawMaterial();
//        existingMaterial.setIdMaterial(materialId);
//        existingMaterial.setName("Old Material");
//
//        RawMaterial otherMaterial = new RawMaterial();
//        otherMaterial.setIdMaterial(2L);
//        otherMaterial.setName("Other Material");
//
//        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.of(existingMaterial));
//        when(rawMaterialRepository.findByName(updatedDTO.getName())).thenReturn(Optional.of(otherMaterial));
//
//        // When: on essaie de changer le nom avec un nom qui existe déjà
//        // Then: une exception DuplicateResourceException est levée
//        assertThrows(DuplicateResourceException.class, () -> rawMaterialService.updateRawMaterial(materialId, updatedDTO));
//        verify(rawMaterialRepository).findById(materialId);
//        verify(rawMaterialRepository).findByName(updatedDTO.getName());
//        verify(rawMaterialRepository, never()).save(any(RawMaterial.class));
//    }
//
//    @Test
//    void deleteRawMaterial_Success() {
//        // Given: une matière première qui existe et n'est pas utilisée dans les commandes
//        when(rawMaterialRepository.existsById(materialId)).thenReturn(true);
//        when(supplyOrderLineRepository.isMaterialUsedInOrders(materialId)).thenReturn(false);
//        doNothing().when(rawMaterialRepository).deleteById(materialId);
//
//        // When: on supprime la matière première
//        assertDoesNotThrow(() -> rawMaterialService.deleteRawMaterial(materialId));
//
//        // Then: la suppression est réussie
//        verify(rawMaterialRepository).existsById(materialId);
//        verify(supplyOrderLineRepository).isMaterialUsedInOrders(materialId);
//        verify(rawMaterialRepository).deleteById(materialId);
//    }
//
//    @Test
//    void deleteRawMaterial_NotFound() {
//        // Given: un ID qui n'existe pas
//        when(rawMaterialRepository.existsById(materialId)).thenReturn(false);
//
//        // When: on essaie de supprimer cette matière première inexistante
//        // Then: une exception ResourceNotFoundException est levée
//        assertThrows(ResourceNotFoundException.class, () -> rawMaterialService.deleteRawMaterial(materialId));
//        verify(rawMaterialRepository).existsById(materialId);
//        verify(supplyOrderLineRepository, never()).isMaterialUsedInOrders(anyLong());
//        verify(rawMaterialRepository, never()).deleteById(anyLong());
//    }
//
//    @Test
//    void deleteRawMaterial_InUse() {
//        // Given: une matière première qui est utilisée dans des commandes
//        when(rawMaterialRepository.existsById(materialId)).thenReturn(true);
//        when(supplyOrderLineRepository.isMaterialUsedInOrders(materialId)).thenReturn(true);
//
//        // When: on essaie de supprimer cette matière première en utilisation
//        // Then: une exception ResourceInUseException est levée (protection des données)
//        assertThrows(ResourceInUseException.class, () -> rawMaterialService.deleteRawMaterial(materialId));
//        verify(rawMaterialRepository).existsById(materialId);
//        verify(supplyOrderLineRepository).isMaterialUsedInOrders(materialId);
//        verify(rawMaterialRepository, never()).deleteById(anyLong());
//    }
//
//    @Test
//    void getAllRawMaterials_Success() {
//        // Given: une liste paginée de matières premières
//        Pageable pageable = PageRequest.of(0, 10);
//        List<RawMaterial> materialList = Collections.singletonList(rawMaterial);
//        Page<RawMaterial> materialPage = new PageImpl<>(materialList, pageable, 1);
//
//        when(rawMaterialRepository.findAll(pageable)).thenReturn(materialPage);
//        when(rawMaterialMapper.toDTO(rawMaterial)).thenReturn(rawMaterialDTO);
//
//        // When: on récupère toutes les matières premières
//        Page<RawMaterialDTO> result = rawMaterialService.getAllRawMaterials(pageable);
//
//        // Then: la liste est correctement retournée avec les bonnes données
//        assertNotNull(result);
//        assertEquals(1, result.getTotalElements());
//        assertEquals(rawMaterialDTO.getName(), result.getContent().get(0).getName());
//        verify(rawMaterialRepository).findAll(pageable);
//    }
//
//    @Test
//    void getMaterialsBelowMinStock_Success() {
//        // Given: des matières premières avec un stock inférieur au minimum
//        List<RawMaterial> materialList = Collections.singletonList(rawMaterial);
//        List<RawMaterialDTO> materialDTOList = Collections.singletonList(rawMaterialDTO);
//
//        when(rawMaterialRepository.findMaterialsBelowMinStock()).thenReturn(materialList);
//        when(rawMaterialMapper.toDTOList(materialList)).thenReturn(materialDTOList);
//
//        // When: on récupère les matières premières sous le stock minimum
//        List<RawMaterialDTO> result = rawMaterialService.getMaterialsBelowMinStock();
//
//        // Then: la liste correcte est retournée
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(rawMaterialDTO.getName(), result.get(0).getName());
//        verify(rawMaterialRepository).findMaterialsBelowMinStock();
//    }
//
//    @Test
//    void getRawMaterialById_Success() {
//        // Given: un ID de matière première qui existe
//        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.of(rawMaterial));
//        when(rawMaterialMapper.toDTO(rawMaterial)).thenReturn(rawMaterialDTO);
//
//        // When: on récupère la matière première par son ID
//        RawMaterialDTO result = rawMaterialService.getRawMaterialById(materialId);
//
//        // Then: la matière première est correctement retournée
//        assertNotNull(result);
//        assertEquals(rawMaterialDTO.getName(), result.getName());
//        verify(rawMaterialRepository).findById(materialId);
//    }
//
//    @Test
//    void getRawMaterialById_NotFound() {
//        // Given: un ID qui n'existe pas en base de données
//        when(rawMaterialRepository.findById(materialId)).thenReturn(Optional.empty());
//
//        // When: on essaie de récupérer cette matière première inexistante
//        // Then: une exception ResourceNotFoundException est levée
//        assertThrows(ResourceNotFoundException.class, () -> rawMaterialService.getRawMaterialById(materialId));
//        verify(rawMaterialRepository).findById(materialId);
//    }
}
