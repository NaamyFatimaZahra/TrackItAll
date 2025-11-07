package org.trackitall.trackitall.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.delivery.dto.CustomerRequestDTO;
import org.trackitall.trackitall.delivery.dto.CustomerResponseDTO;
import org.trackitall.trackitall.delivery.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Customer toEntity(CustomerRequestDTO dto);

    CustomerResponseDTO toResponseDTO(Customer entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateEntityFromDTO(CustomerRequestDTO dto, @MappingTarget Customer entity);
}