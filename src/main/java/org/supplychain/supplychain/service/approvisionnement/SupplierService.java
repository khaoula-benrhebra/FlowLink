package org.supplychain.supplychain.service.approvisionnement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.supplychain.supplychain.dto.Approvisionnement.SupplierDTO;

public interface SupplierService {

    // Ajouter un fournisseur
    SupplierDTO createSupplier(SupplierDTO supplierDTO);

    // Modifier un fournisseur existant
    SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO);

    // Supprimer un fournisseur uniquement s'il n'a aucune commande active
    void deleteSupplier(Long id);

    //  Consulter la liste complète des fournisseurs avec pagination
    Page<SupplierDTO> getAllSuppliers(Pageable pageable);

    //  Rechercher un fournisseur par nom
    Page<SupplierDTO> searchSuppliersByName(String name, Pageable pageable);

    // Méthode  pour récupérer un fournisseur par ID
    SupplierDTO getSupplierById(Long id);
}