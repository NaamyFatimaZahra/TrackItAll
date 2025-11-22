package org.trackitall.trackitall.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.trackitall.trackitall.common.dto.UpdateUserRole;
import org.trackitall.trackitall.common.dto.UserRequestDTO;
import org.trackitall.trackitall.common.dto.UserResponseDTO;
import org.trackitall.trackitall.common.model.User;
import org.trackitall.trackitall.enums.UserRole;
import org.trackitall.trackitall.supply.dto.SupplierRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplierRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplierResponseDTO;
import org.trackitall.trackitall.supply.entity.Supplier;

import java.util.Locale;

@Mapper(componentModel = "spring")
public interface UserMapper {

//    default UserRole mapRole(String role) {
//        if (role == null) return null;
//      return UserRole.valueOf(role.toUpperCase());
//    }
    User toEntity(UserRequestDTO dto);
    UserResponseDTO toResponseDTO(User entity);
    default UserRole toUserRole(UpdateUserRole role){
        return UserRole.valueOf(role.getRole().toUpperCase());
    };
}

