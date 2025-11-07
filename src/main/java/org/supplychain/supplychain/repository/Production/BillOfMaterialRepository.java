package org.supplychain.supplychain.repository.Production;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.model.BillOfMaterial;

import java.util.List;

@Repository
public interface BillOfMaterialRepository extends JpaRepository<BillOfMaterial, Long> {

    List<BillOfMaterial> findByProduct_IdProduct(Long productId);

    void deleteByProduct_IdProduct(Long productId);
}
