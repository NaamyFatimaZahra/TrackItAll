package org.trackitall.trackitall.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.delivery.dto.OrderRequestDTO;
import org.trackitall.trackitall.delivery.dto.OrderResponseDTO;
import org.trackitall.trackitall.delivery.entity.Order;
import org.trackitall.trackitall.production.mapper.ProductMapper;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, ProductMapper.class})
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PREPARING")
    @Mapping(target = "customer", source = "customerId")
    @Mapping(target = "product", source = "productId")
    Order toEntity(OrderRequestDTO dto);

    OrderResponseDTO toResponseDTO(Order entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "customer", source = "customerId")
    @Mapping(target = "product", source = "productId")
    void updateEntityFromDTO(OrderRequestDTO dto, @MappingTarget Order entity);
}