// SupplyOrderItemRequestDTO.java
package org.trackitall.trackitall.supply.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jdk.dynalink.linker.LinkerServices;
import lombok.Data;

import java.util.List;

@Data
public class SupplyOrderItemRequestDTO {
    @NotNull(message = "L'ID de la matière première est obligatoire")
    private Long rawMaterialId;
    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1,message = "la quantity ne peut pas etre inferieur a 1")
    private Integer quantity;
}