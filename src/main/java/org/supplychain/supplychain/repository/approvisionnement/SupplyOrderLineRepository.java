package org.supplychain.supplychain.repository.approvisionnement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.model.SupplyOrderLine;

import java.util.List;

@Repository
public interface SupplyOrderLineRepository extends JpaRepository<SupplyOrderLine, Long> {

    //Récupérer les lignes d'une commande
    List<SupplyOrderLine> findBySupplyOrder_IdOrder(Long orderId);

    // Vérifier si une matière première est utilisée dans des commandes
    @Query("SELECT COUNT(sol) > 0 FROM SupplyOrderLine sol WHERE sol.rawMaterial.idMaterial = :materialId")
    boolean isMaterialUsedInOrders(@Param("materialId") Long materialId);
}