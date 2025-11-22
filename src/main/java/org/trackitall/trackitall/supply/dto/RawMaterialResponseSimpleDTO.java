package org.trackitall.trackitall.supply.dto;

import lombok.Data;

@Data
public class RawMaterialResponseSimpleDTO {
    private Long id;
    private String name;
    private Integer stock;
    private Integer stockMin;
    private String unit;
}
