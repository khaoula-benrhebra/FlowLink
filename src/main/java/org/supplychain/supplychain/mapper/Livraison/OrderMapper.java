package org.supplychain.supplychain.mapper.Livraison;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Livraison.OrderDTO;
import org.supplychain.supplychain.model.Order;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    @Mapping(source = "customer.idCustomer", target = "customerId")
    @Mapping(source = "customer.name", target = "customerName")
    OrderDTO toDTO(Order order);

    List<OrderDTO> toDTOList(List<Order> orders);

    @Mapping(target = "idOrder", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "productOrders", ignore = true)
    @Mapping(target = "delivery", ignore = true)
    Order toEntity(OrderDTO dto);

    @Mapping(target = "idOrder", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "productOrders", ignore = true)
    @Mapping(target = "delivery", ignore = true)
    void updateEntityFromDTO(OrderDTO dto, @MappingTarget Order order);
}