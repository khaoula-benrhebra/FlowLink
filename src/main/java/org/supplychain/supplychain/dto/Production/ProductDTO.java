package org.supplychain.supplychain.dto.Production;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long idProduct;

    @NotNull(message = "Le nom du produit est obligatoire")
    private String name;

    private String description;

    @NotNull(message = "Le temps de production est obligatoire")
    private Integer productionTime;

    @NotNull(message = "Le coût est obligatoire")
    private BigDecimal cost;

    private Integer stock;
    private Integer minimumStock;

    @NotNull(message = "L'unité est obligatoire")
    private String unit;

    // BOM obligatoire à la création
    @NotNull(message = "Le Bill of Materials est obligatoire")
    @NotEmpty(message = "Le Bill of Materials doit contenir au moins une matière première")
    @Valid
    private List<BillOfMaterialDTO> billOfMaterials = new ArrayList<>();
}