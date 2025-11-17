package org.supplychain.supplychain.service.approvisionnement;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Approvisionnement.SupplierDTO;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Approvisionnement.SupplierMapper;
import org.supplychain.supplychain.model.Supplier;
import org.supplychain.supplychain.repository.approvisionnement.SupplierRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplyOrderRepository;
import org.supplychain.supplychain.service.approvisionnement.SupplierService;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplyOrderRepository supplyOrderRepository;
    private final SupplierMapper supplierMapper;

    //Ajouter un fournisseur
    @Override
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        if (supplierRepository.findByName(supplierDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Supplier", "name", supplierDTO.getName());
        }
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDTO(savedSupplier);
    }

    // Modifier un fournisseur existant
    @Override
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));

        supplierRepository.findByName(supplierDTO.getName())
                .ifPresent(supplier -> {
                    if (!supplier.getIdSupplier().equals(id)) {
                        throw new DuplicateResourceException("Supplier", "name", supplierDTO.getName());
                    }
                });

        supplierMapper.updateEntityFromDTO(supplierDTO, existingSupplier);
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);

        return supplierMapper.toDTO(updatedSupplier);
    }

    // Supprimer un fournisseur uniquement s'il n'a aucune commande active
    @Override
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier", "id", id);
        }

        if (supplyOrderRepository.hasActiveOrders(id)) {
            throw new ResourceInUseException("Supplier", id, "active supply orders");
        }

        supplierRepository.deleteById(id);
    }

    // Consulter la liste complète des fournisseurs avec pagination
    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> getAllSuppliers(Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findAll(pageable);
        return suppliers.map(supplierMapper::toDTO);
    }

    // Rechercher un fournisseur par nom
    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> searchSuppliersByName(String name, Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findByNameContainingIgnoreCase(name, pageable);
        return suppliers.map(supplierMapper::toDTO);
    }

    //Méthode  pour récupérer un fournisseur par ID
    @Override
    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        return supplierMapper.toDTO(supplier);
    }
}