package org.supplychain.supplychain.dto.Approvisionnement;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class OrderLineResponse {
    private Long idLine;
    private Long rawMaterialId;
    private String rawMaterialName;
    private Integer quantity;
    private BigDecimal unitPrice;
}