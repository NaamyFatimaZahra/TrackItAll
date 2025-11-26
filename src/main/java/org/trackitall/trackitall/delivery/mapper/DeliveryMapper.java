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
public interface DeliveryMapper {

     Delivery toEntity(DeliveryRequestDTO dto);
     DeliveryResponseDTO toResponseDTO(Delivery entity);


     void updateEntityFromDTO(DeliveryRequestDTO dto, @MappingTarget Delivery entity);

}