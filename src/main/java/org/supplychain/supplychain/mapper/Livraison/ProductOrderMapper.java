package org.supplychain.supplychain.mapper.Livraison;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Livraison.ProductOrderDTO;
import org.supplychain.supplychain.model.ProductOrder;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductOrderMapper {

    @Mapping(source = "order.idOrder", target = "orderId")
    @Mapping(source = "product.idProduct", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    ProductOrderDTO toDTO(ProductOrder productOrder);

    List<ProductOrderDTO> toDTOList(List<ProductOrder> productOrders);

    @Mapping(target = "idProductOrder", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductOrder toEntity(ProductOrderDTO dto);

    @Mapping(target = "idProductOrder", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateEntityFromDTO(ProductOrderDTO dto, @MappingTarget ProductOrder productOrder);
}