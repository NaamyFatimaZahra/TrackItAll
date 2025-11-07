package org.trackitall.trackitall.production.dto;

import lombok.Data;
import org.trackitall.trackitall.supply.dto.RawMaterialResponseDTO;

@Data
public class BillOfMaterialResponseDTO {
    private Long id;
    private RawMaterialResponseDTO material;
    private Integer quantity;
}