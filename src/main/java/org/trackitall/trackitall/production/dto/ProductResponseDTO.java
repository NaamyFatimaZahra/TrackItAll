package org.trackitall.trackitall.production.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String reference;
    private Integer productionTime;
    private Double cost;
    private Integer stock;
    private List<BillOfMaterialResponseDTO> billOfMaterials;
    private Integer activeProductionOrdersCount;
}