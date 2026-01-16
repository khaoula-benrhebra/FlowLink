package org.supplychain.supplychain.service.approvisionnement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Approvisionnement.SupplierRequest;
import org.supplychain.supplychain.dto.Approvisionnement.SupplierResponse;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Approvisionnement.SupplierMapper;
import org.supplychain.supplychain.model.Supplier;
import org.supplychain.supplychain.repository.approvisionnement.SupplierRepository;
import org.supplychain.supplychain.repository.approvisionnement.SupplyOrderRepository;
@Service
@RequiredArgsConstructor
@Transactional
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplyOrderRepository supplyOrderRepository;
    private final SupplierMapper supplierMapper;
    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        if (supplierRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("Supplier", "name", request.getName());
        }
        Supplier supplier = supplierMapper.toEntity(request);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDTO(savedSupplier);
    }
    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        supplierRepository.findByName(request.getName())
                .ifPresent(supplier -> {
                    if (!supplier.getIdSupplier().equals(id)) {
                        throw new DuplicateResourceException("Supplier", "name", request.getName());
                    }
                });
        supplierMapper.updateEntityFromDTO(request, existingSupplier);
        Supplier updatedSupplier = supplierRepository.save(existingSupplier);
        return supplierMapper.toDTO(updatedSupplier);
    }
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
    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponse> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(supplierMapper::toDTO);
    }
    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponse> searchSuppliersByName(String name, Pageable pageable) {
        return supplierRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(supplierMapper::toDTO);
    }
    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        return supplierMapper.toDTO(supplier);
    }
}