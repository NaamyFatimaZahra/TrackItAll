// RawMaterialResponseDTO.java
package org.trackitall.trackitall.supply.dto;

import lombok.Data;
import java.util.List;

@Data
public class RawMaterialResponseDTO {
    private Long id;
    private String name;
    private Integer stock;
    private Integer stockMin;
    private String unit;
    private List<Long> supplierIds; // IDs des fournisseurs associés
}