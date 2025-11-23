package org.trackitall.trackitall.supply.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.trackitall.trackitall.supply.dto.*;
import org.trackitall.trackitall.supply.entity.SupplyOrder;
import org.trackitall.trackitall.supply.entity.SupplyOrderRawMaterial;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplyOrderMapper {
    SupplyOrder toEntity(SupplyOrderRequestDTO dto);

    SupplyOrderResponseDTO toResponseDTO(SupplyOrder entity);


}
