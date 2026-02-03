package org.supplychain.supplychain.mapper.Approvisionnement;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Approvisionnement.*;
import org.supplychain.supplychain.model.Supplier;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        RawMaterialMapper.class })
public interface SupplierMapper {

    @Mapping(target = "materials", qualifiedByName = "toResponseWithoutSuppliers")
    SupplierResponse toDTO(Supplier supplier);

    @Named("toResponseWithoutMaterials")
    @Mapping(target = "materials", ignore = true)
    SupplierResponse toResponseWithoutMaterials(Supplier supplier);

    List<SupplierResponse> toDTOList(List<Supplier> suppliers);

    @Mapping(target = "idSupplier", ignore = true)
    @Mapping(target = "supplyOrders", ignore = true)
    @Mapping(target = "materials", ignore = true)
    Supplier toEntity(SupplierRequest dto);

    @Mapping(target = "idSupplier", ignore = true)
    @Mapping(target = "supplyOrders", ignore = true)
    @Mapping(target = "materials", ignore = true)
    void updateEntityFromDTO(SupplierRequest dto, @MappingTarget Supplier supplier);
}