package org.supplychain.supplychain.mapper;

import org.mapstruct.*;
import org.supplychain.supplychain.dto.UserDTO;
import org.supplychain.supplychain.dto.UserResponse; // Import du nouveau fichier
import org.supplychain.supplychain.model.AppUser;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDTO toDTO(AppUser user);

    UserResponse toResponse(AppUser user);

    @Mapping(target = "idUser", ignore = true)
    AppUser toEntity(UserDTO dto);

    @Mapping(target = "idUser", ignore = true)
    void updateEntityFromDTO(UserDTO dto, @MappingTarget AppUser user);
}