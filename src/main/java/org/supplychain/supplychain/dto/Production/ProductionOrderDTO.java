package org.supplychain.supplychain.dto.Production;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.supplychain.enums.Priority;
import org.supplychain.supplychain.enums.ProductionOrderStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionOrderDTO {

    private Long idOrder;

    @NotNull(message = "L'ID du produit est obligatoire")
    private Long productId;

    private String productName;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;

    private ProductionOrderStatus status;

    @NotNull(message = "La priorité est obligatoire")
    private Priority priority;

    private LocalDate startDate;


    private LocalDate endDate;
}