package org.trackitall.trackitall.production.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trackitall.trackitall.production.dto.BillOfMaterialRequestDTO;
import org.trackitall.trackitall.production.dto.BillOfMaterialResponseDTO;
import org.trackitall.trackitall.production.entity.BillOfMaterial;

@Mapper(componentModel = "spring")
public interface BillOfMaterialMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "material", source = "materialId")
    BillOfMaterial toEntity(BillOfMaterialRequestDTO dto);

    BillOfMaterialResponseDTO toResponseDTO(BillOfMaterial entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "material", source = "materialId")
    void updateEntityFromDTO(BillOfMaterialRequestDTO dto, @MappingTarget BillOfMaterial entity);
}