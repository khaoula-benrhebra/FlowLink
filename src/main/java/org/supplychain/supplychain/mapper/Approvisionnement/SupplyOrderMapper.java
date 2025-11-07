package org.supplychain.supplychain.mapper.Approvisionnement;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Approvisionnement.SupplyOrderDTO;
import org.supplychain.supplychain.model.SupplyOrder;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SupplyOrderMapper {

    @Mapping(source = "supplier.idSupplier", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    SupplyOrderDTO toDTO(SupplyOrder supplyOrder);

    List<SupplyOrderDTO> toDTOList(List<SupplyOrder> supplyOrders);

    @Mapping(target = "idOrder", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "orderLines", ignore = true)
    SupplyOrder toEntity(SupplyOrderDTO dto);

    @Mapping(target = "idOrder", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "orderLines", ignore = true)
    void updateEntityFromDTO(SupplyOrderDTO dto, @MappingTarget SupplyOrder supplyOrder);
}