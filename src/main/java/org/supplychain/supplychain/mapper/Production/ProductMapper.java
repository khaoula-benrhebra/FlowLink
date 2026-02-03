package org.supplychain.supplychain.mapper.Production;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Production.ProductDTO;
import org.supplychain.supplychain.model.Product;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
        BillOfMaterialMapper.class })
public interface ProductMapper {

    ProductDTO toDTO(Product product);

    List<ProductDTO> toDTOList(List<Product> products);

    @Mapping(target = "idProduct", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    @Mapping(target = "productionOrders", ignore = true)
    @Mapping(target = "productOrders", ignore = true)
    @Mapping(target = "stock", source = "stock", defaultValue = "0")
    @Mapping(target = "minimumStock", source = "minimumStock", defaultValue = "0")
    Product toEntity(ProductDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idProduct", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    @Mapping(target = "productionOrders", ignore = true)
    @Mapping(target = "productOrders", ignore = true)
    void updateEntityFromDTO(ProductDTO dto, @MappingTarget Product product);
}