package org.trackitall.trackitall.supply.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.trackitall.trackitall.supply.dto.*;
import org.trackitall.trackitall.supply.entity.SupplyOrder;
import org.trackitall.trackitall.supply.entity.SupplyOrderRawMaterial;
import java.util.List;

@Mapper(componentModel = "spring", uses = {SupplierMapper.class, RawMaterialMapper.class})
public interface SupplyOrderMapper {


    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "rawMaterials", ignore = true)
    SupplyOrder toEntity(SupplyOrderRequestDTO dto);

    SupplyOrderResponseDTO toResponseDTO(SupplyOrder entity);


}
