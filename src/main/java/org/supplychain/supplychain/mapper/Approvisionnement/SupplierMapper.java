package org.supplychain.supplychain.mapper.Approvisionnement;
import org.mapstruct.*;
import org.supplychain.supplychain.dto.Approvisionnement.SupplierRequest;
import org.supplychain.supplychain.dto.Approvisionnement.SupplierResponse;
import org.supplychain.supplychain.model.Supplier;
import java.util.List;
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SupplierMapper {
    SupplierResponse toDTO(Supplier supplier);
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