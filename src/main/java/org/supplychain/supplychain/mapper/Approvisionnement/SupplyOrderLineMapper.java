package org.supplychain.supplychain.mapper.Approvisionnement;
import org.mapstruct.*;
import org.supplychain.supplychain.dto.Approvisionnement.OrderLineResponse;
import org.supplychain.supplychain.model.SupplyOrderLine;
import java.util.List;
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplyOrderLineMapper {
    @Mapping(source = "rawMaterial.idMaterial", target = "rawMaterialId")
    @Mapping(source = "rawMaterial.name", target = "rawMaterialName")
    OrderLineResponse toResponse(SupplyOrderLine supplyOrderLine);
    List<OrderLineResponse> toResponseList(List<SupplyOrderLine> lines);
}