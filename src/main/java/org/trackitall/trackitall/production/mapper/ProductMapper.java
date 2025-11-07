package org.trackitall.trackitall.production.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.production.dto.ProductRequestDTO;
import org.trackitall.trackitall.production.dto.ProductResponseDTO;
import org.trackitall.trackitall.production.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    @Mapping(target = "productionOrders", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Product toEntity(ProductRequestDTO dto);

    ProductResponseDTO toResponseDTO(Product entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "billOfMaterials", ignore = true)
    @Mapping(target = "productionOrders", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateEntityFromDTO(ProductRequestDTO dto, @MappingTarget Product entity);
}