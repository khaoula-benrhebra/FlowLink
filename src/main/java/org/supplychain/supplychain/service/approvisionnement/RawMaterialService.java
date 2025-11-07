package org.supplychain.supplychain.service.approvisionnement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.Approvisionnement.RawMaterialDTO;

import java.util.List;

public interface RawMaterialService {

    // Ajouter une matière première
    RawMaterialDTO createRawMaterial(RawMaterialDTO rawMaterialDTO);

    // Modifier une matière première
    RawMaterialDTO updateRawMaterial(Long id, RawMaterialDTO rawMaterialDTO);

    // Supprimer une matière première si elle n'est pas utilisée
    void deleteRawMaterial(Long id);

    // Consulter la liste complète des matières premières avec pagination
    Page<RawMaterialDTO> getAllRawMaterials(Pageable pageable);

    // Consulter les matières dont le stock est inférieur au seuil critique
    List<RawMaterialDTO> getMaterialsBelowMinStock();

    // Méthode  pour récupérer une matière première par ID
    RawMaterialDTO getRawMaterialById(Long id);
}