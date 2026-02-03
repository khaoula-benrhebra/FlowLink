package org.supplychain.supplychain.mapper.Livraison;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.Livraison.CustomerRequestDTO;
import org.supplychain.supplychain.dto.Livraison.CustomerResponseDTO;
import org.supplychain.supplychain.model.Customer;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
    @Mapping(source = "idCustomer", target = "id")
    @Mapping(target = "ordersCount", ignore = true)
    @Mapping(target = "hasActiveOrders", ignore = true)
    CustomerResponseDTO toResponseDTO(Customer customer);

    List<CustomerResponseDTO> toResponseDTOList(List<Customer> customers);

    @Mapping(target = "idCustomer", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Customer toEntity(CustomerRequestDTO dto);

    @Mapping(target = "idCustomer", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateEntityFromDTO(CustomerRequestDTO dto, @MappingTarget Customer customer);
}