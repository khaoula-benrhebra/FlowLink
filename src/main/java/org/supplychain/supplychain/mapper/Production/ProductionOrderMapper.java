package org.supplychain.supplychain.mapper.Production;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Production.ProductionOrderDTO;
import org.supplychain.supplychain.model.ProductionOrder;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductionOrderMapper {

    @Mapping(source = "product.idProduct", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    ProductionOrderDTO toDTO(ProductionOrder productionOrder);

    List<ProductionOrderDTO> toDTOList(List<ProductionOrder> productionOrders);

    @Mapping(target = "idOrder", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    ProductionOrder toEntity(ProductionOrderDTO dto);

    @Mapping(target = "idOrder", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    void updateEntityFromDTO(ProductionOrderDTO dto, @MappingTarget ProductionOrder productionOrder);
}