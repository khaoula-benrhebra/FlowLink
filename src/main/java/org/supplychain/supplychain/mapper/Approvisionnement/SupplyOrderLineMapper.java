package org.supplychain.supplychain.mapper.Approvisionnement;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Approvisionnement.SupplyOrderLineDTO;
import org.supplychain.supplychain.model.SupplyOrderLine;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SupplyOrderLineMapper {

    @Mapping(source = "supplyOrder.idOrder", target = "supplyOrderId")
    @Mapping(source = "rawMaterial.idMaterial", target = "rawMaterialId")
    @Mapping(source = "rawMaterial.name", target = "rawMaterialName")
    SupplyOrderLineDTO toDTO(SupplyOrderLine supplyOrderLine);

    List<SupplyOrderLineDTO> toDTOList(List<SupplyOrderLine> supplyOrderLines);

    @Mapping(target = "idLine", ignore = true)
    @Mapping(target = "supplyOrder", ignore = true)
    @Mapping(target = "rawMaterial", ignore = true)
    SupplyOrderLine toEntity(SupplyOrderLineDTO dto);

    @Mapping(target = "idLine", ignore = true)
    @Mapping(target = "supplyOrder", ignore = true)
    @Mapping(target = "rawMaterial", ignore = true)
    void updateEntityFromDTO(SupplyOrderLineDTO dto, @MappingTarget SupplyOrderLine supplyOrderLine);
}