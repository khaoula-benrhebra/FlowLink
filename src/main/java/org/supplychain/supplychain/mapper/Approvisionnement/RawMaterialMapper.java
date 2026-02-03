package org.supplychain.supplychain.mapper.Approvisionnement;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Approvisionnement.*;
import org.supplychain.supplychain.model.RawMaterial;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        SupplierMapper.class })
public interface RawMaterialMapper {

    @Mapping(target = "suppliers", qualifiedByName = "toResponseWithoutMaterials")
    MaterialResponse toDTO(RawMaterial rawMaterial);

    @Named("toResponseWithoutSuppliers")
    @Mapping(target = "suppliers", ignore = true)
    MaterialResponse toResponseWithoutSuppliers(RawMaterial rawMaterial);

    List<MaterialResponse> toDTOList(List<RawMaterial> rawMaterials);

    @Mapping(target = "idMaterial", ignore = true)
    @Mapping(target = "suppliers", ignore = true)
    @Mapping(target = "supplyOrderLines", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    RawMaterial toEntity(MaterialRequest dto);

    @Mapping(target = "idMaterial", ignore = true)
    @Mapping(target = "suppliers", ignore = true)
    @Mapping(target = "supplyOrderLines", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    void updateEntityFromDTO(MaterialRequest dto, @MappingTarget RawMaterial rawMaterial);
}