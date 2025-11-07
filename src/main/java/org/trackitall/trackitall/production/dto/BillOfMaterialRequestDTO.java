package org.trackitall.trackitall.production.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BillOfMaterialRequestDTO {
    @NotNull(message = "La matière première est obligatoire")
    private Long materialId;

    @NotNull(message = "La quantité est obligatoire")
    private Integer quantity;
}