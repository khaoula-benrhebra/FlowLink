package org.supplychain.supplychain.dto.Livraison;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.supplychain.supplychain.enums.OrderStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    @NotNull(message = "L'ID du client est obligatoire")
    private Long customerId;

    private String customerName;

    @NotNull(message = "Le statut de la commande est obligatoire")
    private OrderStatus status;

    @NotEmpty(message = "La liste des produits ne peut pas être vide")
    @Valid
    private List<ProductOrderDTO> productOrders;
}