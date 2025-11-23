// SupplyOrderItemRequestDTO.java
package org.trackitall.trackitall.supply.dto;

import jakarta.validation.constraints.NotNull;
import jdk.dynalink.linker.LinkerServices;
import lombok.Data;

import java.util.List;

@Data
public class SupplyOrderItemRequestDTO {
    @NotNull(message = "L'ID de la matière première est obligatoire")
    private Integer rawMaterialId;

    @NotNull(message = "La quantité est obligatoire")
    private Integer quantity;
}