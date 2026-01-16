package org.supplychain.supplychain.service.approvisionnement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.Approvisionnement.MaterialRequest;
import org.supplychain.supplychain.dto.Approvisionnement.MaterialResponse;
import java.util.List;
public interface RawMaterialService {
    MaterialResponse createRawMaterial(MaterialRequest request);
    MaterialResponse updateRawMaterial(Long id, MaterialRequest request);
    void deleteRawMaterial(Long id);
    Page<MaterialResponse> getAllRawMaterials(Pageable pageable);
    List<MaterialResponse> getMaterialsBelowMinStock();
    MaterialResponse getRawMaterialById(Long id);
}