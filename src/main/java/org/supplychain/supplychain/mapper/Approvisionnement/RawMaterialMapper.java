package org.supplychain.supplychain.mapper.Approvisionnement;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Approvisionnement.MaterialRequest;
import org.supplychain.supplychain.dto.Approvisionnement.MaterialResponse;
import org.supplychain.supplychain.model.RawMaterial;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        SupplierMapper.class })
public interface RawMaterialMapper {

    @Mapping(target = "suppliers", source = "suppliers")
    MaterialResponse toDTO(RawMaterial rawMaterial);

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