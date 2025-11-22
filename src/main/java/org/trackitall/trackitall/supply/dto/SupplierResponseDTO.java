// SupplierResponseDTO.java
package org.trackitall.trackitall.supply.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SupplierResponseDTO {
    private int id;
    private String name;
    private String contact;
    private Double rating;
    private int leadTime;
    private List<RawMaterialResponseSimpleDTO> rawMaterials;
}