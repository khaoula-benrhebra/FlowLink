package org.supplychain.supplychain.dto.Approvisionnement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponse {
    private Long idSupplier;
    private String name;
    private String contact;
    private String email;
    private String phone;
    private Double rating;
    private Integer leadTime;
}