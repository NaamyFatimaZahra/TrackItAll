// SupplyOrderItemResponseDTO.java
package org.trackitall.trackitall.supply.dto;

import lombok.Data;


@Data
public class SupplyOrderItemResponseDTO {
    private RawMaterialResponseDTO rawMaterial;
    private Integer quantity;
}