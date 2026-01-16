package org.supplychain.supplychain.dto.Approvisionnement;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class MaterialRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String name;
    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    private Integer stock;
    @NotNull(message = "Le stock minimum est obligatoire")
    @Min(value = 0, message = "Le stock minimum ne peut pas être négatif")
    private Integer stockMin;
    @NotBlank(message = "L'unité est obligatoire")
    private String unit;

    // Liste des IDs des fournisseurs à associer à cette matière première
    private List<Long> supplierIds;
}