package org.trackitall.trackitall.production.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.production.dto.ProductionOrderRequestDTO;
import org.trackitall.trackitall.production.dto.ProductionOrderResponseDTO;
import org.trackitall.trackitall.production.entity.ProductionOrder;
import org.trackitall.trackitall.production.entity.Product;
import org.trackitall.trackitall.production.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public interface  ProductionOrderMapper {

     ProductionOrder toEntity(ProductionOrderRequestDTO dto);

   ProductionOrderResponseDTO toResponseDTO(ProductionOrder entity);

}