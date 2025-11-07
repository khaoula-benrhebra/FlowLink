package org.supplychain.supplychain.dto.Approvisionnement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.supplychain.enums.SupplyOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyOrderDTO {

    @NotBlank(message = "Le numéro de commande est obligatoire")
    private String orderNumber;

    @NotNull(message = "L'ID du fournisseur est obligatoire")
    private Long supplierId;

    private String supplierName;

    @NotNull(message = "La date de commande est obligatoire")
    private LocalDate orderDate;

    @NotNull(message = "Le statut est obligatoire")
    private SupplyOrderStatus status;

    @DecimalMin(value = "0.0", inclusive = false, message = "Le montant total doit être positif")
    private BigDecimal totalAmount;

    @NotNull(message = "Les lignes de commande sont obligatoires")
    @NotEmpty(message = "La commande doit contenir au moins une ligne")
    @Valid
    private List<SupplyOrderLineDTO> orderLines;
}