package org.supplychain.supplychain.dto.Approvisionnement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class OrderLineRequest {
    @NotNull(message = "L'ID de la matière première est obligatoire")
    private Long rawMaterialId;
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;
    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix unitaire doit être positif")
    private BigDecimal unitPrice;
}