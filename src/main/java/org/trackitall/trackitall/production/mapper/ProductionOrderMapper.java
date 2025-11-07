package org.trackitall.trackitall.production.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.production.dto.ProductionOrderRequestDTO;
import org.trackitall.trackitall.production.dto.ProductionOrderResponseDTO;
import org.trackitall.trackitall.production.entity.ProductionOrder;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface ProductionOrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "product", source = "productId")
    ProductionOrder toEntity(ProductionOrderRequestDTO dto);

    ProductionOrderResponseDTO toResponseDTO(ProductionOrder entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "product", source = "productId")
    void updateEntityFromDTO(ProductionOrderRequestDTO dto, @MappingTarget ProductionOrder entity);
}