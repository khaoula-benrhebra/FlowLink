package org.supplychain.supplychain.dto.Approvisionnement;

import lombok.Data;

import java.util.List;

@Data
public class MaterialResponse {
    private Long idMaterial;
    private String name;
    private Integer stock;
    private Integer stockMin;
    private String unit;

    // Liste des fournisseurs associés à cette matière première
    private List<SupplierResponse> suppliers;
}