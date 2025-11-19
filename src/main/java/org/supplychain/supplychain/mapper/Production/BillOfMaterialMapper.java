package org.supplychain.supplychain.mapper.Production;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.supplychain.supplychain.dto.Production.BillOfMaterialDTO;
import org.supplychain.supplychain.model.BillOfMaterial;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BillOfMaterialMapper {

    @Mapping(source = "material.idMaterial", target = "materialId")
    @Mapping(source = "material.name", target = "materialName")
    @Mapping(source = "material.unit", target = "materialUnit")
    BillOfMaterialDTO toDTO(BillOfMaterial bom);

    @Mapping(target = "idBOM", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "material", ignore = true)
    BillOfMaterial toEntity(BillOfMaterialDTO dto);

    @Mapping(target = "idBOM", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "material", ignore = true)
    void updateEntityFromDTO(BillOfMaterialDTO dto, @MappingTarget BillOfMaterial bom);
}
