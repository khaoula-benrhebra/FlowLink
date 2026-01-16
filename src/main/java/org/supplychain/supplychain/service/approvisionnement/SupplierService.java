package org.supplychain.supplychain.service.approvisionnement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.Approvisionnement.SupplierRequest;
import org.supplychain.supplychain.dto.Approvisionnement.SupplierResponse;
public interface SupplierService {
    SupplierResponse createSupplier(SupplierRequest request);
    SupplierResponse updateSupplier(Long id, SupplierRequest request);
    void deleteSupplier(Long id);
    Page<SupplierResponse> getAllSuppliers(Pageable pageable);
    Page<SupplierResponse> searchSuppliersByName(String name, Pageable pageable);
    SupplierResponse getSupplierById(Long id);
}