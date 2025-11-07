package org.supplychain.supplychain.dto.Livraison;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.supplychain.enums.DeliveryStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDTO {

    @NotNull(message = "L'ID de la commande est obligatoire")
    private Long orderId;

    @NotBlank(message = "L'adresse de livraison est obligatoire")
    private String deliveryAddress;

    private String driver;

    @NotNull(message = "Le statut de livraison est obligatoire")
    private DeliveryStatus status;

    private LocalDate deliveryDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le coût de livraison doit être positif")
    private BigDecimal deliveryCost;
}