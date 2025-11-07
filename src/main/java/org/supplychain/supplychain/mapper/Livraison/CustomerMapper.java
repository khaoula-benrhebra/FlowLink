package org.supplychain.supplychain.mapper.Livraison;


import org.mapstruct.*;
import org.supplychain.supplychain.dto.Livraison.CustomerDTO;
import org.supplychain.supplychain.model.Customer;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CustomerMapper {

    CustomerDTO toDTO(Customer customer);

    List<CustomerDTO> toDTOList(List<Customer> customers);

    @Mapping(target = "idCustomer", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Customer toEntity(CustomerDTO dto);

    @Mapping(target = "idCustomer", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateEntityFromDTO(CustomerDTO dto, @MappingTarget Customer customer);
}
