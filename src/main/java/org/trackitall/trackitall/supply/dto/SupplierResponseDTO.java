// SupplierResponseDTO.java
package org.trackitall.trackitall.supply.dto;

import lombok.Data;
import java.util.List;

@Data
public class SupplierResponseDTO {
    private Long id;
    private String name;
    private String contact;
    private Double rating;
    private Integer leadTime;
    private List<RawMaterialResponseDTO> rawMaterials;
}