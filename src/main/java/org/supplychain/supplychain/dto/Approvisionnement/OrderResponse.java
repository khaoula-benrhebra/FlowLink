package org.supplychain.supplychain.dto.Approvisionnement;
import lombok.Data;
import org.supplychain.supplychain.enums.SupplyOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
public class OrderResponse {
    private Long idOrder;
    private Long supplierId;
    private String supplierName;
    private LocalDate orderDate;
    private SupplyOrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderLineResponse> orderLines;
}