package org.trackitall.trackitall.supply.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.trackitall.trackitall.supply.dto.SupplierRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplierRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplierResponseDTO;
import org.trackitall.trackitall.supply.entity.Supplier;

@Mapper(componentModel = "spring", uses = RawMaterialMapper.class)
public interface SupplierMapper {

    Supplier toEntity(SupplierRequestDTO dto);

    @Mapping(target = "rawMaterials", qualifiedByName = "withStock")
    SupplierResponseDTO toResponseDTO(Supplier entity);
}

