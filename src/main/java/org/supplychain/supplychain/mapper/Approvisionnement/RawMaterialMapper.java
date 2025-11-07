package org.supplychain.supplychain.mapper.Approvisionnement;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Approvisionnement.RawMaterialDTO;
import org.supplychain.supplychain.model.RawMaterial;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RawMaterialMapper {

    RawMaterialDTO toDTO(RawMaterial rawMaterial);

    List<RawMaterialDTO> toDTOList(List<RawMaterial> rawMaterials);

    @Mapping(target = "idMaterial", ignore = true)
    @Mapping(target = "suppliers", ignore = true)
    @Mapping(target = "supplyOrderLines", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    RawMaterial toEntity(RawMaterialDTO dto);

    @Mapping(target = "idMaterial", ignore = true)
    @Mapping(target = "suppliers", ignore = true)
    @Mapping(target = "supplyOrderLines", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    void updateEntityFromDTO(RawMaterialDTO dto, @MappingTarget RawMaterial rawMaterial);
}