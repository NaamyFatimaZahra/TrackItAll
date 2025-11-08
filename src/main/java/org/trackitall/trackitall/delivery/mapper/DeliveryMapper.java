package org.trackitall.trackitall.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.delivery.dto.DeliveryRequestDTO;
import org.trackitall.trackitall.delivery.dto.DeliveryResponseDTO;
import org.trackitall.trackitall.delivery.entity.Delivery;
import org.trackitall.trackitall.delivery.entity.Order;
import org.trackitall.trackitall.delivery.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class DeliveryMapper {

    @Autowired
    protected OrderRepository orderRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PLANIFEE")
    @Mapping(target = "order", source = "orderId")
    public abstract Delivery toEntity(DeliveryRequestDTO dto);

    public abstract DeliveryResponseDTO toResponseDTO(Delivery entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "order", source = "orderId")
    public abstract void updateEntityFromDTO(DeliveryRequestDTO dto, @MappingTarget Delivery entity);

    protected Order map(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
}