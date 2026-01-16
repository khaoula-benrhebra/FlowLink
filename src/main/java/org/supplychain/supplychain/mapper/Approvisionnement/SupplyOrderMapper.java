package org.supplychain.supplychain.mapper.Approvisionnement;
import org.mapstruct.*;
import org.supplychain.supplychain.dto.Approvisionnement.OrderResponse;
import org.supplychain.supplychain.model.SupplyOrder;
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {SupplyOrderLineMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplyOrderMapper {
    @Mapping(source = "supplier.idSupplier", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    @Mapping(source = "orderLines", target = "orderLines")
    OrderResponse toResponse(SupplyOrder supplyOrder);
}