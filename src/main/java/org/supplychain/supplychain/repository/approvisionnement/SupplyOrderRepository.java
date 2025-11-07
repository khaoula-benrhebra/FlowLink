package org.supplychain.supplychain.repository.approvisionnement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.enums.SupplyOrderStatus;
import org.supplychain.supplychain.model.SupplyOrder;

import java.util.Optional;

@Repository
public interface SupplyOrderRepository extends JpaRepository<SupplyOrder, Long> {

    //  Vérifier l'unicité du numéro de commande
    Optional<SupplyOrder> findByOrderNumber(String orderNumber);

    //Consulter la liste complète des commandes avec pagination
    Page<SupplyOrder> findAll(Pageable pageable);

    //  Suivre le statut des commandes
    Page<SupplyOrder> findByStatus(SupplyOrderStatus status, Pageable pageable);

    // Vérifier si un fournisseur a des commandes actives
    @Query("SELECT COUNT(s) > 0 FROM SupplyOrder s WHERE s.supplier.idSupplier = :supplierId AND s.status != 'REÇUE'")
    boolean hasActiveOrders(@Param("supplierId") Long supplierId);
}