// SupplyOrderItemRequestDTO.java
package org.trackitall.trackitall.supply.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SupplyOrderItemRequestDTO {
    @NotNull(message = "L'ID de la matière première est obligatoire")
    private Long rawMaterialId;

    @NotNull(message = "La quantité est obligatoire")
    private Integer quantity;
}