package org.supplychain.supplychain.mapper.Production;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.supplychain.supplychain.dto.Production.BillOfMaterialDTO;
import org.supplychain.supplychain.model.BillOfMaterial;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BillOfMaterialMapper {

    @Mapping(source = "material.idMaterial", target = "materialId")
    @Mapping(source = "material.name", target = "materialName")
    @Mapping(source = "material.unit", target = "materialUnit")
    BillOfMaterialDTO toDTO(BillOfMaterial bom);
}
