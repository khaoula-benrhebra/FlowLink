package org.supplychain.supplychain.repository.approvisionnement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.supplychain.supplychain.model.RawMaterial;

import java.util.List;
import java.util.Optional;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {

    //Consulter les matières dont le stock est inférieur au seuil critique
    @Query("SELECT r FROM RawMaterial r WHERE r.stock < r.stockMin")
    List<RawMaterial> findMaterialsBelowMinStock();

    //Rechercher une matière première par nom
    Optional<RawMaterial> findByName(String name);

    // Consulter la liste complète des matières premières avec pagination
    Page<RawMaterial> findAll(Pageable pageable);
}