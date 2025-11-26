package org.trackitall.trackitall.production.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.production.dto.ProductRequestDTO;
import org.trackitall.trackitall.production.dto.ProductResponseDTO;
import org.trackitall.trackitall.production.entity.Product;

@Mapper(componentModel = "spring")

public interface ProductMapper {


    Product toEntity(ProductRequestDTO dto);

    ProductResponseDTO toResponseDTO(Product entity);


    void updateEntityFromDTO(ProductRequestDTO dto, @MappingTarget Product entity);
}