package org.trackitall.trackitall.supply.mapper;

import ch.qos.logback.core.model.ComponentModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.trackitall.trackitall.supply.dto.SupplyOrderItemRequestDTO;
import org.trackitall.trackitall.supply.entity.SupplyOrderRawMaterial;

@Mapper(componentModel = "Spring")
public interface SupplyOrderItemsMapper {
    SupplyOrderRawMaterial toEntity(SupplyOrderItemRequestDTO supplyOrderItemRequestDTO);
}
