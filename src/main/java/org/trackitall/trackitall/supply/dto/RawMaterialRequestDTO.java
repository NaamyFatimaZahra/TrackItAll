// RawMaterialRequestDTO.java
package org.trackitall.trackitall.supply.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class RawMaterialRequestDTO {
    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @NotNull(message = "Le stock est obligatoire")
    private Integer stock;

    @NotNull(message = "Le stock minimum est obligatoire")
    private Integer stockMin;

    @NotBlank(message = "L'unité est obligatoire")
    private String unit;

    private List<Long> supplierIds;
}