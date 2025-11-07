package org.supplychain.supplychain.repository.approvisionnement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.model.Supplier;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    //  Rechercher un fournisseur par nom
    Page<Supplier> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Vérifier l'unicité du nom
    Optional<Supplier> findByName(String name);

    // Consulter la liste complète des fournisseurs avec pagination
    Page<Supplier> findAll(Pageable pageable);
}