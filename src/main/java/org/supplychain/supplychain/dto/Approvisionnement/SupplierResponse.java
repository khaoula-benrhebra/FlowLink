package org.supplychain.supplychain.dto.Approvisionnement;

import lombok.Data;
import java.util.List;

@Data
public class SupplierResponse {
    private Long idSupplier;
    private String name;
    private String contact;
    private String email;
    private String phone;
    private Double rating;
    private Integer leadTime;

    private List<MaterialResponse> materials;
}