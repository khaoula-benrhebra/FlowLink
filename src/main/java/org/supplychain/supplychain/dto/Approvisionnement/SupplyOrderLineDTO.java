package org.supplychain.supplychain.dto.Approvisionnement;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyOrderLineDTO {

    @NotNull(message = "L'ID de la commande d'approvisionnement est obligatoire")
    private Long supplyOrderId;

    @NotNull(message = "L'ID de la matière première est obligatoire")
    private Long rawMaterialId;

    private String rawMaterialName;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    private BigDecimal unitPrice;
}