package org.supplychain.supplychain.dto.Livraison;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String city;
    // Statistiques (pour le détail uniquement)
    private Integer ordersCount;
    private Boolean hasActiveOrders;
}