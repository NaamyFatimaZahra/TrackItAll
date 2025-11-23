package org.trackitall.trackitall.supply.mapper;

import org.mapstruct.Mapper;
import org.trackitall.trackitall.supply.dto.SupplierRequestDTO;
import org.trackitall.trackitall.supply.dto.SupplierResponseDTO;
import org.trackitall.trackitall.supply.entity.Supplier;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    Supplier toEntity(SupplierRequestDTO dto);
    SupplierResponseDTO toResponseDTO(Supplier entity);
}

