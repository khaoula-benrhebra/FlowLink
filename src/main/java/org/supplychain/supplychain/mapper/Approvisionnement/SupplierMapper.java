package org.supplychain.supplychain.mapper.Approvisionnement;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Approvisionnement.SupplierDTO;
import org.supplychain.supplychain.model.Supplier;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SupplierMapper {

    SupplierDTO toDTO(Supplier supplier);

    List<SupplierDTO> toDTOList(List<Supplier> suppliers);

    @Mapping(target = "idSupplier", ignore = true)
    @Mapping(target = "supplyOrders", ignore = true)
    @Mapping(target = "materials", ignore = true)
    Supplier toEntity(SupplierDTO dto);

    @Mapping(target = "idSupplier", ignore = true)
    @Mapping(target = "supplyOrders", ignore = true)
    @Mapping(target = "materials", ignore = true)
    void updateEntityFromDTO(SupplierDTO dto, @MappingTarget Supplier supplier);
}