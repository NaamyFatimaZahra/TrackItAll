package org.trackitall.trackitall.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.delivery.dto.DeliveryRequestDTO;
import org.trackitall.trackitall.delivery.dto.DeliveryResponseDTO;
import org.trackitall.trackitall.delivery.entity.Delivery;

@Mapper(componentModel = "spring", uses = {OrderMapper.class})
public interface DeliveryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PLANNED")
    @Mapping(target = "order", source = "orderId")
    Delivery toEntity(DeliveryRequestDTO dto);

    DeliveryResponseDTO toResponseDTO(Delivery entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "order", source = "orderId")
    void updateEntityFromDTO(DeliveryRequestDTO dto, @MappingTarget Delivery entity);
}