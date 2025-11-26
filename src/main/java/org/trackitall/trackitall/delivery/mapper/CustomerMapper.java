package org.trackitall.trackitall.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.delivery.dto.CustomerRequestDTO;
import org.trackitall.trackitall.delivery.dto.CustomerResponseDTO;
import org.trackitall.trackitall.delivery.entity.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {


    Customer toEntity(CustomerRequestDTO dto);

    CustomerResponseDTO toResponseDTO(Customer entity);

    void updateEntityFromDTO(CustomerRequestDTO dto, @MappingTarget Customer entity);
}