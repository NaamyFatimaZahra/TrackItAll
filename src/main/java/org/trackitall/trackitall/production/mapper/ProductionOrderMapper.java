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
public abstract class ProductionOrderMapper {

    @Autowired
    protected ProductRepository productRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "EN_ATTENTE")
    @Mapping(target = "product", source = "productId")
    public abstract ProductionOrder toEntity(ProductionOrderRequestDTO dto);

    public abstract ProductionOrderResponseDTO toResponseDTO(ProductionOrder entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "product", source = "productId")
    public abstract void updateEntityFromDTO(ProductionOrderRequestDTO dto, @MappingTarget ProductionOrder entity);

    protected Product map(Long productId) {
        return productRepository.findById(productId).orElse(null);
    }
}