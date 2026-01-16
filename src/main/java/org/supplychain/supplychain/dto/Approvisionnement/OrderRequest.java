package org.supplychain.supplychain.dto.Approvisionnement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
@Data
public class OrderRequest {
    @NotNull(message = "L'ID du fournisseur est obligatoire")
    private Long supplierId;
    @NotNull(message = "La date de commande est obligatoire")
    private LocalDate orderDate;
    @NotNull(message = "Les lignes de commande sont obligatoires")
    @NotEmpty(message = "La commande doit contenir au moins une ligne")
    @Valid
    private List<OrderLineRequest> orderLines;
}