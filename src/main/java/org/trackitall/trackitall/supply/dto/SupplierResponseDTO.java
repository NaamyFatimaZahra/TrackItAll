package org.trackitall.trackitall.supply.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class SupplierResponseDTO {
    private int id;
    private String name;
    private String contact;
    private Double rating;
    private int leadTime;
    private List<RawMaterialResponseSimpleDTO> rawMaterials;
}