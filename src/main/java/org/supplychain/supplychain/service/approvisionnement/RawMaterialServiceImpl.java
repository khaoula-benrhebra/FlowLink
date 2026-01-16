package org.supplychain.supplychain.service.approvisionnement;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Approvisionnement.MaterialRequest;
import org.supplychain.supplychain.dto.Approvisionnement.MaterialResponse;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Approvisionnement.RawMaterialMapper;
import org.supplychain.supplychain.model.RawMaterial;
import org.supplychain.supplychain.model.Supplier;
import org.supplychain.supplychain.repository.approvisionnement.RawMaterialRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplierRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplyOrderLineRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RawMaterialServiceImpl implements RawMaterialService {
    private final RawMaterialRepository rawMaterialRepository;
    private final SupplyOrderLineRepository supplyOrderLineRepository;
    private final SupplierRepository supplierRepository;
    private final RawMaterialMapper rawMaterialMapper;

    @Override
    public MaterialResponse createRawMaterial(MaterialRequest request) {
        if (rawMaterialRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("RawMaterial", "name", request.getName());
        }
        RawMaterial entity = rawMaterialMapper.toEntity(request);

        // Associer les suppliers si fournis
        if (request.getSupplierIds() != null && !request.getSupplierIds().isEmpty()) {
            List<Supplier> suppliers = supplierRepository.findAllById(request.getSupplierIds());
            entity.setSuppliers(new ArrayList<>(suppliers));
        }

        return rawMaterialMapper.toDTO(rawMaterialRepository.save(entity));
    }

    @Override
    public MaterialResponse updateRawMaterial(Long id, MaterialRequest request) {
        RawMaterial existing = rawMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RawMaterial", "id", id));
        rawMaterialMapper.updateEntityFromDTO(request, existing);

        // Mettre à jour les suppliers
        if (request.getSupplierIds() != null) {
            List<Supplier> suppliers = supplierRepository.findAllById(request.getSupplierIds());
            existing.setSuppliers(new ArrayList<>(suppliers));
        } else {
            // Si supplierIds est null, on vide la liste (optionnel, selon le besoin)
            existing.getSuppliers().clear();
        }

        return rawMaterialMapper.toDTO(rawMaterialRepository.save(existing));
    }

    @Override
    public void deleteRawMaterial(Long id) {
        if (!rawMaterialRepository.existsById(id)) {
            throw new ResourceNotFoundException("RawMaterial", "id", id);
        }
        if (supplyOrderLineRepository.isMaterialUsedInOrders(id)) {
            throw new ResourceInUseException("RawMaterial", id, "commandes existantes");
        }
        rawMaterialRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaterialResponse> getAllRawMaterials(Pageable pageable) {
        return rawMaterialRepository.findAll(pageable).map(rawMaterialMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialResponse> getMaterialsBelowMinStock() {
        return rawMaterialMapper.toDTOList(rawMaterialRepository.findMaterialsBelowMinStock());
    }

    @Override
    @Transactional(readOnly = true)
    public MaterialResponse getRawMaterialById(Long id) {
        return rawMaterialRepository.findById(id)
                .map(rawMaterialMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("RawMaterial", "id", id));
    }
}