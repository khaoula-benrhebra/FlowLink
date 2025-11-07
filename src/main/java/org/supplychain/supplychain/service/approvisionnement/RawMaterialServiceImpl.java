package org.supplychain.supplychain.service.approvisionnement;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Approvisionnement.RawMaterialDTO;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Approvisionnement.RawMaterialMapper;
import org.supplychain.supplychain.model.RawMaterial;
import org.supplychain.supplychain.repository.approvisionnement.RawMaterialRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplyOrderLineRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RawMaterialServiceImpl implements RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final SupplyOrderLineRepository supplyOrderLineRepository;
    private final RawMaterialMapper rawMaterialMapper;

    // Ajouter une matière première

    @Override
    public RawMaterialDTO createRawMaterial(RawMaterialDTO rawMaterialDTO) {
        if (rawMaterialRepository.findByName(rawMaterialDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("RawMaterial", "name", rawMaterialDTO.getName());
        }

        RawMaterial rawMaterial = rawMaterialMapper.toEntity(rawMaterialDTO);

        RawMaterial savedMaterial = rawMaterialRepository.save(rawMaterial);

        return rawMaterialMapper.toDTO(savedMaterial);
    }

    //User Story: Modifier une matière première
    @Override
    public RawMaterialDTO updateRawMaterial(Long id, RawMaterialDTO rawMaterialDTO) {
        RawMaterial existingMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RawMaterial", "id", id));

        rawMaterialRepository.findByName(rawMaterialDTO.getName())
                .ifPresent(material -> {
                    if (!material.getIdMaterial().equals(id)) {
                        throw new DuplicateResourceException("RawMaterial", "name", rawMaterialDTO.getName());
                    }
                });
        rawMaterialMapper.updateEntityFromDTO(rawMaterialDTO, existingMaterial);
        RawMaterial updatedMaterial = rawMaterialRepository.save(existingMaterial);
        return rawMaterialMapper.toDTO(updatedMaterial);
    }

    // User Story: Supprimer une matière première si elle n'est pas utilisée
    @Override
    public void deleteRawMaterial(Long id) {
        if (!rawMaterialRepository.existsById(id)) {
            throw new ResourceNotFoundException("RawMaterial", "id", id);
        }

        // Vérifier si la matière première est utilisée dans des commandes
        if (supplyOrderLineRepository.isMaterialUsedInOrders(id)) {
            throw new ResourceInUseException("RawMaterial", id, "supply orders");
        }
        rawMaterialRepository.deleteById(id);
    }

    // Consulter la liste complète des matières premières avec pagination
    @Override
    @Transactional(readOnly = true)
    public Page<RawMaterialDTO> getAllRawMaterials(Pageable pageable) {
        Page<RawMaterial> rawMaterials = rawMaterialRepository.findAll(pageable);
        return rawMaterials.map(rawMaterialMapper::toDTO);
    }

    // Consulter les matières dont le stock est inférieur au seuil critique
    @Override
    @Transactional(readOnly = true)
    public List<RawMaterialDTO> getMaterialsBelowMinStock() {
        List<RawMaterial> materials = rawMaterialRepository.findMaterialsBelowMinStock();
        return rawMaterialMapper.toDTOList(materials);
    }

    // Méthode pour récupérer une matière première par ID
    @Override
    @Transactional(readOnly = true)
    public RawMaterialDTO getRawMaterialById(Long id) {
        RawMaterial rawMaterial = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RawMaterial", "id", id));
        return rawMaterialMapper.toDTO(rawMaterial);
    }
}