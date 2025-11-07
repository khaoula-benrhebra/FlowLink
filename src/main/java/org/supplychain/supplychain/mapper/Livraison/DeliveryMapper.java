package org.supplychain.supplychain.mapper.modelDelivery;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Livraison.DeliveryDTO;
import org.supplychain.supplychain.model.Delivery;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DeliveryMapper {

    @Mapping(source = "order.idOrder", target = "orderId")
    DeliveryDTO toDTO(Delivery delivery);

    List<DeliveryDTO> toDTOList(List<Delivery> deliveries);

    @Mapping(target = "idDelivery", ignore = true)
    @Mapping(target = "order", ignore = true)
    Delivery toEntity(DeliveryDTO dto);

    @Mapping(target = "idDelivery", ignore = true)
    @Mapping(target = "order", ignore = true)
    void updateEntityFromDTO(DeliveryDTO dto, @MappingTarget Delivery delivery);
}