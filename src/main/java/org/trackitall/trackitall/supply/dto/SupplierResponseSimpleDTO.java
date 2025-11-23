package org.trackitall.trackitall.supply.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SupplierResponseSimpleDTO {
    private int id;
    private String name;
    private String contact;
    private Double rating;
    private int leadTime;
}