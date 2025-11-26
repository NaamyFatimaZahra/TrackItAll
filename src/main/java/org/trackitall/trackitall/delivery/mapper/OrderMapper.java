package org.trackitall.trackitall.delivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.delivery.dto.OrderRequestDTO;
import org.trackitall.trackitall.delivery.dto.OrderResponseDTO;
import org.trackitall.trackitall.delivery.entity.Order;
import org.trackitall.trackitall.delivery.entity.Customer;
import org.trackitall.trackitall.production.entity.Product;
import org.trackitall.trackitall.delivery.repository.CustomerRepository;
import org.trackitall.trackitall.production.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface  OrderMapper {



      Order toEntity(OrderRequestDTO dto);

      OrderResponseDTO toResponseDTO(Order entity);


      void updateEntityFromDTO(OrderRequestDTO dto, @MappingTarget Order entity);


}