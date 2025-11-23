// SupplyOrderItemResponseDTO.java
package org.trackitall.trackitall.supply.dto;

import lombok.Data;


@Data
public class SupplyOrderItemResponseDTO {
    private RawMaterialResponseSimpleDTO rawMaterial;
    private Integer quantity;
}