package org.trackitall.trackitall.production.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class ProductRequestDTO {
    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    private String reference;

    @NotNull(message = "Le temps de production est obligatoire")
    private Integer productionTime;

    @NotNull(message = "Le coût est obligatoire")
    private Double cost;

    @NotNull(message = "Le stock est obligatoire")
    private Integer stock;

    private List<BillOfMaterialRequestDTO> billOfMaterials;
}