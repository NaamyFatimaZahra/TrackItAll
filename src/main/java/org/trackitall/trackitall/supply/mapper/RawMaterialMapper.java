package org.trackitall.trackitall.supply.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.trackitall.trackitall.supply.dto.RawMaterialRequestDTO;
import org.trackitall.trackitall.supply.dto.RawMaterialResponseDTO;
import org.trackitall.trackitall.supply.dto.RawMaterialResponseSimpleDTO;
import org.trackitall.trackitall.supply.entity.RawMaterial;

@Mapper(componentModel = "spring")
public interface RawMaterialMapper {

    RawMaterial toEntity(RawMaterialRequestDTO dto);

    @Named("basic")
    RawMaterialResponseDTO toResponseDTO(RawMaterial entity);

    RawMaterialResponseDTO toResponseDTOWithStockInfo(RawMaterial entity);
    @Named("withStock")
    RawMaterialResponseSimpleDTO toResponseSimple(RawMaterial rawMaterial);
}
