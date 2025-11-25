package org.trackitall.trackitall.production.dto;

import lombok.Data;
import org.trackitall.trackitall.supply.dto.RawMaterialResponseDTO;
import org.trackitall.trackitall.supply.dto.RawMaterialResponseSimpleDTO;

@Data
public class BillOfMaterialResponseDTO {
    private Long id;
    private RawMaterialResponseSimpleDTO material;
    private Integer quantity;
}